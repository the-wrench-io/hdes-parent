package io.resys.hdes.backend.spi.storage;

import io.resys.hdes.backend.api.HdesBackend.DefType;
import io.resys.hdes.backend.spi.util.Assert;
import io.resys.hdes.backend.spi.util.Assert.HdesUIBackendApiExeption;

public class HdesResourceBuilder {
  
  private String name;
  private DefType type;
  
  public HdesResourceBuilder type(DefType type) {
    this.type = type;
    return this;
  }
  
  public HdesResourceBuilder name(String name) {
    this.name = name;
    return this;
  }
  
  public String build() {
    Assert.notEmpty(name, () -> "name can't be null!");
    Assert.notNull(type, () -> "type can't be null!");
    
    // TODO::
    switch (type) {
    case DT: return new StringBuilder()
      .append("define decision-table: ").append(name).append("\n")
      .append("headers: {\n")
      .append("  name STRING required IN,\n")
      .append("  lastName STRING required IN,\n") 
      .append("  type INTEGER required IN,\n")
      .append("  value INTEGER required OUT \n") 
      .append("} ALL: {\n")
      .append("  { ?, ?, between 1 and 30, 20 },\n")
      .append("  { not 'bob' or 'sam', 'woman', ?, 4570 }\n") 
      .append("}")
      .toString();
    
    case FL: return new StringBuilder()
      .append("define flow: ").append(name).append(" description: 'descriptive'\n")
      .append("headers: {\n")
      .append("  id INTEGER optional IN,\n") 
      .append("  externalId INTEGER required IN,\n") 
      .append("  elements ARRAY of OBJECT required IN: {\n") 
      .append("    value STRING required IN\n")
      .append("  },\n") 
      .append("  output ARRAY of OBJECT required OUT: {\n") 
      .append("    value STRING required OUT\n")
      .append("  }\n")
      .append("}\n")
      .append("tasks: {\n") 
      .append("  FirstTask: {\n") 
      .append("    then: HandleElement\n") 
      .append("    decision-table: bestDtTask uses: {\n") 
      .append("      name: elements.value\n")
      .append("    }\n")
      .append("  } from elements then: EndTask,\n") 
      .append("  \n")
      .append("  HandleElement: {\n") 
      .append("    then: end as: { output: { value: FirstTask.dtOutput }}\n") 
      .append("  },\n")
      .append("  \n")
      .append("  EndTask: {\n") 
      .append("    then: end as: { input1: arg1.x1, input2: arg2.x1 }\n") 
      .append("  }\n")
      .append("}").toString();

    case MT: return new StringBuilder()
      .append("define manual-task: ").append(name).append(" \n")
      .append("description: 'very descriptive manual task'\n") 
      .append("\n")
      .append("headers: {\n") 
      .append("  customers ARRAY of OBJECT required IN: {\n") 
      .append("    firstName INTEGER optional IN,\n")
      .append("    lastName  INTEGER required IN\n") 
      .append("  },\n")
      .append("  avgAge INTEGER required OUT formula: sum(out.age)/out.length\n") 
      .append("}\n")
      .append("\n") 
      .append("dropdowns: {\n") 
      .append("  gender: { 0: 'female', 1: 'male', 2: 'other' }\n") 
      .append("}\n")
      .append("\n") 
      .append("actions: { \n") 
      .append("  when validateAge:     age > 30   then show error message: 'you are too old',  // context params: 0 - validateAge, 1 - age, 2 - 30, message - 'you are to old'\n") 
      .append("  when validateAge:     age > 30   then show info message:  'you are too old',\n")
      .append("  when otherGender:     gender = 2 then show group:         subGenderGroup,\n")
      .append("  when otherMaleGender: gender = 1 then show field:         subGenderGroup\n") 
      .append("}\n")
      .append("\n") 
      .append("form of groups: { \n") 
      .append("  cars:  { fields: {} },\n") 
      .append("  boats: { fields: {} },\n") 
      .append("  soups: { groups: {} },\n") 
      .append("  ppl:   { fields: {\n")
      .append("    firstName STRING  required: { default-value: 'BOB' class: 'super-style-1 super-style-2' },\n") 
      .append("    age       INTEGER required: { default-value: 1 },\n")
      .append("    gender    INTEGER optional: { single-choice dropdown: genderDropdown }\n") 
      .append("  }}\n")
      .append("} from customers").toString();
    case ST:
    case TG:
    default: throw new HdesUIBackendApiExeption("Resource builder for type: " + type + " is not implemented!");
    }
  }
  
  public static HdesResourceBuilder builder() {
    return new HdesResourceBuilder();
  }
}
