parser grammar FlowParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

dataType
  : ObjectDataType
  | ScalarType;

flow: id description? inputs tasks EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: task (',' task)*;
taskBody: conditionalThen;

conditionalThen: whenThenArgs | then | endMapping;
whenThenArgs: whenThen (',' whenThen)*;
whenThen: 'when' ':' StringLiteral then; 
then: 'then' ':' typeName ('END' | taskRef)?;

taskRef: ObjectDataType TaskType ':' typeName mapping;
mapping: 'mapping' ':' '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

typeName : Identifier | typeName '.' Identifier;
id: 'id' ':' typeName;
description: 'description' ':' literal;
input: RequiredType dataType typeName debugValue?;
debugValue: 'debugValue' ':' literal;
task: typeName ':' '{' taskBody? '}';
mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;
endMapping: 'END' mapping;


