parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

headerType: ScalarType;
directionType: DirectionType;
dt: id description? headers hitPolicy EOF;

hitPolicy: first | all | matrix;

first: 'FIRST' ':' '{' rulesets? '}';
all: 'ALL' ':' '{' rulesets? '}';
matrix: 'MATRIX' ':' '{' rulesets? '}';

headers: 'headers' ':' '{' headerArgs? '}';
headerArgs: header (',' header)*;
header: directionType headerType typeName;

rulesets: ruleset (',' ruleset)*;
ruleset: '{' rules? '}';
rules: value (',' value)*;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
value: undefinedValue | literal;
undefinedValue: '?';
