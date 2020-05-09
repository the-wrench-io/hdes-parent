parser grammar CommonParser;
options { tokenVocab = HdesLexer; }
import ExpressionParser;

directionType: DirectionType;
scalarType: ScalarType;
description: 'description' ':' literal;

headers: 'headers' ':' typeDefs;

typeDefs: '{' typeDefArgs? '}';
typeDefArgs: typeDef (',' typeDef)*;
typeDef: typeName (arrayType | objectType | simpleType);

simpleType: scalarType RequiredType directionType formula? debugValue?;
objectType: 'OBJECT' RequiredType directionType ':' typeDefs;
arrayType: 'ARRAY' 'of' (simpleType | objectType);
debugValue: 'debug-value' ':' literal;
formula: 'formula' ':' enBody;
