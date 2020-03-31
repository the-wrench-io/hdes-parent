parser grammar FlowParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;
typeName : Identifier | typeName '.' Identifier;
description: 'description' ':' literal;

flow: id description? inputs tasks EOF;

inputs: 'inputs' ':' '{' input* '}';
tasks: 'tasks' ':' '{' task* '}';
task: typeName ':' '{' taskBody? '}';
taskBody: conditionalThen;

conditionalThen: whenThen* | then;
whenThen: 'when' ':' StringLiteral then; 
then: 'then' ':' typeName taskRef?;

taskRef: ObjectDataType TaskType ':' typeName mapping;
mapping: 'mapping' ':' '{' mappingArg* '}';
mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;

id: 'id' ':' typeName;
debugValue: 'debugValue' ':' literal?;
input: 'required'? DataType typeName debugValue?;








