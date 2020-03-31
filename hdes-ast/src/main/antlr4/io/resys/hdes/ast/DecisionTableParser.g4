parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;
  
id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
hitPolicy: HitPolicyType;

dt: id hitPolicy description? headers values EOF;

headers: 'headers' ':' '{' (',' header)* '}';
values: 'values' ':' '{' (',' value)* '}';

header: DirectionType ScalarTypes typeName;
value: literal;

