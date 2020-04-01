parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

headerType: ScalarType;

dt: id description? hitPolicy headers values EOF;

headers: 'headers' ':' '{' headerArgs? '}';
headerArgs: header (',' header)*;
header: DirectionType headerType typeName;

values: 'values' ':' '{' rulesets? '}';
rulesets: ruleset (',' ruleset)*;
ruleset: '{' rules? '}';
rules: value (',' value)*;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
hitPolicy: 'hitPolicy' ':' HitPolicyType;
value: '?' | literal;
