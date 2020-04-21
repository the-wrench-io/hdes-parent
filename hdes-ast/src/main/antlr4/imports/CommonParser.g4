parser grammar CommonParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

scalarType: ScalarType;
typeName : Identifier | typeName '.' Identifier;
id: 'id' ':' typeName;
description: 'description' ':' literal;

typeDefs: '{' typeDefArgs? '}';
typeDefArgs: typeDef (',' typeDef)*;
typeDef: typeName (arrayType | objectType | simpleType);

simpleType: scalarType RequiredType debugValue?;
objectType: 'OBJECT' RequiredType ':' typeDefs;
arrayType: 'ARRAY' (simpleType | objectType);
debugValue: 'debugValue' ':' literal;
