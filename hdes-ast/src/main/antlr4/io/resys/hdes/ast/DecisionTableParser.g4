parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;
  
dt: id hitPolicy description? headers values EOF;

headers: 'headers' ':' '{' headerArgs? '}';
headerArgs: header (',' header)*;
values: 'values' ':' '{' valuesArgs? '}';
valuesArgs: value (',' value)*;

header: DirectionType ScalarTypes typeName;
value: literal;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
hitPolicy: HitPolicyType;