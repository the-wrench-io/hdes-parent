parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


directionType: DirectionType;
dt: id description? headers hitPolicy EOF;

hitPolicy: first | all | matrix;

first: 'FIRST' ':' '{' rulesets? '}';
all: 'ALL' ':' '{' rulesets? '}';
matrix: 'MATRIX' ':' '{' rulesets? '}';

headers: 'headers' ':' '{' headerArgs? '}';
headerArgs: header (',' header)*;
header: directionType scalarType typeName;

rulesets: ruleset (',' ruleset)*;
ruleset: '{' rules? '}';
rules: value (',' value)*;

value: undefinedValue | matchingExpression | equalityExpression;

matchingExpression: (NOT_OP)? orExpression;
orExpression: literal | (OR literal)*;

equalityExpression
  : relationalExpression 
  | relationalExpression AND relationalExpression
  | relationalExpression OR relationalExpression;

relationalExpression
  : '=' literal
  | '!=' literal
  | '<' literal
  | '<=' literal
  | '>' literal
  | '>=' literal;

undefinedValue: '?';
