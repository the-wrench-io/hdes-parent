parser grammar FlowParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

taskTypes
  : MANUAL_TASK
  | FLOW_TASK 
  | DT_TASK
  | ST_TASK;

typeName : Identifier | typeName '.' Identifier;
id: 'id' ':' typeName;
description: 'description' ':' literal;
scalarType: ScalarType;

flow: id description? inputs tasks EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: task (',' task)*;
task: endTask | nextTask;
endTask: END ':' '{' mapping '}';
nextTask: typeName ':' '{' (pointer taskRef?)? '}';

pointer: whenThenArgs | then;
whenThenArgs: whenThen (',' whenThen)*;
whenThen: 'when' ':' whenExpression then; 
whenExpression: StringLiteral;
then: 'then' ':' typeName;

taskRef: taskTypes ':' typeName mapping;  
mapping: ObjectDataType 'mapping' ':' '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

input: RequiredType (simpleType | arrayType | objectType);
simpleType: scalarType typeName debugValue?;
arrayType: 'ARRAY' (simpleType | objectType);
objectType: 'OBJECT' typeName inputs?;

debugValue: 'debugValue' ':' literal;
mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;


