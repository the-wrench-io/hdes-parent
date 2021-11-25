package io.resys.hdes.client.spi.composer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.resys.hdes.client.api.HdesClient;
import io.resys.hdes.client.api.ast.TypeDef;
import io.resys.hdes.client.api.ast.TypeDef.Direction;
import io.resys.hdes.client.api.exceptions.HdesBadRequestException;
import io.resys.hdes.client.api.programs.ProgramEnvir;
import io.resys.hdes.client.api.programs.ProgramEnvir.ProgramWrapper;

public class DebugCSVVisitor {
  private final HdesClient client;
  private final ProgramEnvir envir;
  private final ProgramWrapper<?, ?> wrapper;
  private final Map<String, TypeDef> wrapperInputs;
  private final CsvMapper csvMapper = new CsvMapper();
  private final Set<String> usedFields = new HashSet<>();
  
  public DebugCSVVisitor(HdesClient client, ProgramWrapper<?, ?> wrapper, ProgramEnvir envir) {
    super();
    this.client = client;
    this.wrapper = wrapper;
    this.envir = envir;
    this.wrapperInputs = wrapper.getHeaders().stream()
      .filter(h -> h.getDirection() == Direction.IN)
      .collect(Collectors.toMap(h -> h.getName(), h -> h));
  }

  public String visit(String input) {
    try {
      final CSVParser parser = CSVParser.parse(input, CSVFormat.DEFAULT.withDelimiter(';').withIgnoreEmptyLines());
      final List<CSVRecord> records = parser.getRecords();
      if(records.isEmpty()) {
        return null;
      }
      
      // records
      final Iterator<CSVRecord> iterator = records.iterator();
      
      // header names
      final var headers = visitHeaders(iterator);
    
      // data rows
      final List<Map<String, Serializable>> results = new ArrayList<>();
      while (iterator.hasNext()) {
        final CSVRecord row = iterator.next();
        final var rows = visitRow(row, headers);
        results.addAll(rows);
      }
    
      final CsvSchema.Builder schema = CsvSchema.builder();
      schema.addColumn("_id");
      usedFields.forEach(name -> schema.addColumn(name));
      schema.addColumn("_errors");
      
      return csvMapper.writer(schema.build().withHeader()).writeValueAsString(results);
    } catch(IOException e) {
      throw new HdesBadRequestException(e.getMessage(), e);
    }
  }
  
  private Map<Integer, String> visitHeaders(Iterator<CSVRecord> iterator) {
    final Map<Integer, String> headers = new HashMap<>();
    int headerIndex = 0;
    for (String header : iterator.next()) {
      headers.put(headerIndex++, header);
    }
    return headers;
  }

  public List<Map<String, Serializable>> visitRow(CSVRecord row, Map<Integer, String> headers) {
    long transactionId = row.getRecordNumber();
    try {
      final var input = visitProgramInput(row, headers);
      return visitProgram(input, transactionId);
    } catch (Exception e) {
      return Arrays.asList(Map.of(
          "_id", transactionId,
          "_errors", e.getMessage()
          ));
    }
  }
  
  
  public Map<String, Serializable> visitProgramInput(CSVRecord row, Map<Integer, String> headers) {
    final Map<String, Serializable> inputEntity = new HashMap<>();;
    int columnIndex = 0;
    for(final var columnValue : row) {
      final var columnName = headers.get(columnIndex++);
      final var typeDef = this.wrapperInputs.get(columnName);
      if(typeDef == null) {
        continue;
      }
      inputEntity.put(columnName, typeDef.getDeserializer().deserialize(typeDef, columnValue));
    }
    return inputEntity;
  }
  
  private List<Map<String, Serializable>> visitProgram(Map<String, Serializable> input, long transactionId) throws IOException {
    this.usedFields.addAll(input.keySet());
    
    switch (wrapper.getType()) {
    case FLOW: return visitFlow(input, transactionId);
    case FLOW_TASK: return visitFlowTask(input, transactionId);
    case DT: return visitDecision(input, transactionId);
    default: throw new HdesBadRequestException("Can't debug: '" + wrapper.getType() + "'!");
    }
  }
  
  
  private List<Map<String, Serializable>> visitFlow(Map<String, Serializable> input, long transactionId) {
    final var body = client.executor(envir).inputMap(input).flow(wrapper.getId()).andGetBody(); 
    final var last = body.getReturns();
    return visitProgramBody(last, transactionId);
  }
  
  private List<Map<String, Serializable>> visitFlowTask(Map<String, Serializable> input, long transactionId) {
    final var body = client.executor(envir).inputMap(input).service(wrapper.getId()).andGetBody();
    final var result = new HashMap<String, Serializable>(client.mapper().toMap(body.getValue()));
    return visitProgramBody(result, transactionId);
  }
  
  private List<Map<String, Serializable>> visitDecision(Map<String, Serializable> input, long transactionId) {
    final var body = client.executor(envir).inputMap(input).decision(wrapper.getId()).andGetBody();
    final var result = new HashMap<String, Serializable>(client.mapper().toMap(body.getMatches()));
    return visitProgramBody(result, transactionId);
  }
  
  private List<Map<String, Serializable>> visitProgramBody(Map<String, Serializable> last, long transactionId) {
    if(last.size() == 1) {
      final var firstValue = last.values().iterator().next();
      if(firstValue instanceof Collection) {
        final var rows = new ArrayList<Map<String, Serializable>>();
        for(final var entry : ((Collection<?>) firstValue)) {
          final var result = new HashMap<String, Serializable>();
          result.put("_id", transactionId);
          result.putAll(client.mapper().toMap(entry));
          rows.add(result);
        }
        return rows;
      }
    }
    
    final var results = new HashMap<String, Serializable>(last);
    results.put("_id", transactionId);
    return Arrays.asList(results);
  }
}
