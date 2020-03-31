parser grammar FlowParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

flow: id description? inputs tasks EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: task (',' task)*;
taskBody: conditionalThen;

conditionalThen: whenThen* | then;
whenThen: 'when' ':' StringLiteral then; 
then: 'then' ':' typeName taskRef?;

taskRef: ObjectDataType TaskType ':' typeName mapping;
mapping: 'mapping' ':' '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

typeName : Identifier | typeName '.' Identifier;
id: 'id' ':' typeName;
description: 'description' ':' literal;
input: RequiredType DataType typeName debugValue?;
debugValue: 'debugValue' ':' literal;
task: typeName ':' '{' taskBody? '}';
mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;

