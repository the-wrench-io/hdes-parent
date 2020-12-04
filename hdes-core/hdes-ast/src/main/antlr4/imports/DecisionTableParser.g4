parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;


decisionTableUnit: simpleTypeName '{' headers hitPolicy '}';
hitPolicy: matchingPolicy | mappingPolicy;

matchingPolicy: MATCH (FIRST | ALL) '{' whenThenRules* '}';
whenThenRules: whenRules thenRules;

mappingPolicy: MAP mappingFrom whenRules TO mappingTo mappingRows;
mappingFrom: scalarType;
mappingTo: scalarType;

mappingRows: '{' (mappingRow (mappingRow)*)? '}';
mappingRow: simpleTypeName thenRules;

thenRules: '{' (ruleLiteral (',' ruleLiteral)*)? '}';
whenRules: WHEN '(' (ruleExpression (',' ruleExpression)*)? ')';

ruleLiteral: literal;
ruleExpression: ruleUndefinedValue | expressionUnit;
ruleUndefinedValue: '?';