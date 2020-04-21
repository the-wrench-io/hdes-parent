parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


dtBody: typeName description? headers hitPolicy;

hitPolicy: first | all | matrix;

first: 'FIRST' ':' '{' rulesets? '}';
all: 'ALL' ':' '{' rulesets? '}';
matrix: 'MATRIX' ':' '{' rulesets? '}';

rulesets: ruleset (',' ruleset)*;
ruleset: '{' rules? '}';
rules: ruleValue (',' ruleValue)*;

ruleValue: ruleUndefinedValue | ruleMatchingExpression | ruleEqualityExpression;

ruleMatchingExpression: (NOT_OP)? ruleMatchingOrExpression;
ruleMatchingOrExpression: literal (OR literal)*;

ruleEqualityExpression
  : ruleRelationalExpression 
  | ruleRelationalExpression AND ruleRelationalExpression
  | ruleRelationalExpression OR ruleRelationalExpression;

ruleRelationalExpression
  : '=' literal
  | '!=' literal
  | '<' literal
  | '<=' literal
  | '>' literal
  | '>=' literal;

ruleUndefinedValue: '?';
