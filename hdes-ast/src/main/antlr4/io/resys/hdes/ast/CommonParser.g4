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

inputs: 'inputs' ':' typeDefs;
typeDefs: '{' typeDefArgs? '}';
typeDefArgs: typeDef (',' typeDef)*;
typeDef: RequiredType (arrayType | objectType | simpleType);

simpleType: scalarType typeName debugValue?;
objectType: 'OBJECT' typeName ':' typeDefs;
arrayType: 'ARRAY' (simpleType | objectType);
debugValue: 'debugValue' ':' literal;
