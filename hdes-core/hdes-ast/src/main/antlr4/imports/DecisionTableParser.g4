parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;


dtBody: simpleTypeName '{' headers hitPolicy '}';
hitPolicy: matchingPolicy | mappingPolicy;

matchingPolicy: 'matches' ('FIRST' | 'ALL') '{' whenThenRules* '}';
whenThenRules: 'when' whenRules 'then' thenRules;

mappingPolicy: 'maps' mappingFrom whenRules 'to' mappingTo mappingRows;
mappingFrom: scalarType;
mappingTo: scalarType;

mappingRows: '{' (mappingRow (mappingRow)*)? '}';
mappingRow: simpleTypeName thenRules;

thenRules: '{' (ruleLiteral (',' ruleLiteral)*)? '}';
whenRules: '{' (ruleExpression (',' ruleExpression)*)? '}';

ruleLiteral: literal;
ruleExpression: ruleUndefinedValue | enBody;
ruleUndefinedValue: '?';