parser grammar CommonParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

directionType: DirectionType;
scalarType: ScalarType;
typeName : Identifier | typeName '.' Identifier;
description: 'description' ':' literal;

headers: 'headers' ':' typeDefs;

typeDefs: '{' typeDefArgs? '}';
typeDefArgs: typeDef (',' typeDef)*;
typeDef: typeName (arrayType | objectType | simpleType);

simpleType: scalarType RequiredType directionType debugValue?;
objectType: 'OBJECT' RequiredType directionType ':' typeDefs;
arrayType: 'ARRAY' (simpleType | objectType);
debugValue: 'debugValue' ':' literal;
