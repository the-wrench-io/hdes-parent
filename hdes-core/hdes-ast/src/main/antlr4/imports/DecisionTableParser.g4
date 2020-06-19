parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


dtBody: typeName description? headers hitPolicy;

hitPolicy: first | all | matrix;

first: 'FIRST' ':' '{' rulesets? '}';
all: 'ALL' ':' '{' rulesets? '}';
matrix: 'MATRIX' 'from' scalarType 'to' scalarType ':' '{' (matrixHeaders ',')? matrixRulesets? '}';

matrixHeaders: ruleset;
matrixRulesets: matrixRuleset (',' matrixRuleset)*;
matrixRuleset: typeName ':' '{' matrixRules '}';
matrixRules: matrixRule (',' matrixRule)*;
matrixRule: literal;

rulesets: ruleset (',' ruleset)*;
ruleset: '{' rules? '}';
rules: ruleValue (',' ruleValue)*;

ruleValue: ruleUndefinedValue | ruleMatchingExpression | ruleEqualityExpression;

ruleMatchingExpression: 'not'? ruleMatchingOrExpression;
ruleMatchingOrExpression: literal (OR literal)*;

ruleEqualityExpression
  : ruleRelationalExpression 
  | ruleRelationalExpression AND ruleRelationalExpression
  | ruleRelationalExpression OR ruleRelationalExpression
  | BETWEEN ruleUnaryExpression AND ruleUnaryExpression;

ruleRelationalExpression
  : '=' ruleUnaryExpression
  | '!=' ruleUnaryExpression
  | '<' ruleUnaryExpression
  | '<=' ruleUnaryExpression
  | '>' ruleUnaryExpression
  | '>=' ruleUnaryExpression;

ruleUnaryExpression:  '-' ruleUnaryExpression | literal;
ruleUndefinedValue: '?';
