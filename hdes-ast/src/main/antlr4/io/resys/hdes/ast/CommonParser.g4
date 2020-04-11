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

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;
input: RequiredType (simpleType | arrayType | objectType);
simpleType: scalarType typeName debugValue?;
arrayType: 'ARRAY' (simpleType | objectType);
objectType: 'OBJECT' typeName inputs?;
debugValue: 'debugValue' ':' literal;