parser grammar DecisionTableParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;


decisionTableUnit: simpleTypeName headers '{' hitPolicy '}';
hitPolicy: matchingPolicy | mappingPolicy;

matchingPolicy: (TM_FIND_FIRST | TM_FIND_ALL) '(' '{' whenThenRules* '}' ')';
whenThenRules: WHEN whenRules '.'? ADD_TO '(' thenRules ')';

mappingPolicy: MAP '(' mappingFrom ')' '.'? TO '(' mappingTo ')' '.'? WHEN whenRules mappingRows;
mappingFrom: scalarType;
mappingTo: scalarType;

mappingRows: mappingRow*;
mappingRow: '.'? simpleTypeName '(' thenRules ')';

thenRules: '{' (ruleLiteral (',' ruleLiteral)*)? '}';
whenRules: '(' (ruleExpression (',' ruleExpression)*)? ')';

ruleLiteral: literal;
ruleExpression: ruleUndefinedValue | expressionUnit;
ruleUndefinedValue: '?';