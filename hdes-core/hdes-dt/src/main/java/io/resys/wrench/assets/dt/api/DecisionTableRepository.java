package io.resys.wrench.assets.dt.api;

//public interface DecisionTableRepository {
//
//  DecisionTableBuilder createBuilder();
//  DecisionTableExporter createExporter();
//
//
//  interface DecisionTableExporter {
//    DecisionTableExporter src(AstDecision dt);
//    DecisionTableExporter format(DecisionTableFormat format);
//    String build();
//  }
//
//  interface DecisionTableExecutor {
//    DecisionTableExecutor decisionTable(DecisionProgram decisionTable);
//    DecisionTableExecutor context(Function<TypeDef, Object> context);
//    DecisionResult execute();
//  }
//
//  interface DecisionTableBuilder {
//    DecisionTableBuilder format(DecisionTableFormat format);
//
//    DecisionTableBuilder rename(Optional<String> name);
//    DecisionTableBuilder src(String input);
//    DecisionTableBuilder src(InputStream inputStream);
//    DecisionTableBuilder src(JsonNode src);
//    DecisionProgram build();
//  }
//
//  enum DecisionTableFormat {
//    JSON,
//    CSV
//  }
//
//}
