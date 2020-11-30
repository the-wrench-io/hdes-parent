parser grammar TypeDefParser;
options { tokenVocab = HdesLexer; }
import ExpressionParser;

scalarType: ScalarType;

headers: 'accepts' headersAccepts 'returns' headersReturns;
headersAccepts: typeDefs;
headersReturns: typeDefs;

typeDefs: '{' (typeDef (',' typeDef)*)? '}';
typeDef: typeDefNames (arrayType | objectType | simpleType);
typeDefNames: typeDefName (',' typeDefName)*;
typeDefName: optional? simpleTypeName;

simpleType: scalarType (debugValue? | (formula | formulaOverAll)?);
objectType: 'OBJECT' typeDefs;
arrayType: 'ARRAY' 'of' (simpleType | objectType);

optional: '*';
debugValue: 'debug-value' ':' literal;
formula: ':' enBody;
formulaOverAll: ':' '*' enBody; // apply formula over array of elements


mapping: '{' (mappingArg (',' mappingArg)* )? '}';  
mappingArg: fieldMapping | fastMapping;
fieldMapping: simpleTypeName ':' mappingValue;
fastMapping: typeName;
mappingValue: mapping | enBody;