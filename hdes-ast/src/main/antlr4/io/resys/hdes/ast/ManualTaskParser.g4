parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

scalarType: ScalarType;
dropdownType: DropdownType;
cssIdentifier: CssIdentifier;
mt: id description? inputs dropdowns statements form EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;
input: RequiredType (simpleType | arrayType | objectType);
simpleType: scalarType typeName;
arrayType: 'ARRAY' (simpleType | objectType);
objectType: 'OBJECT' typeName inputs?;

dropdowns: 'dropdowns' ':' '{' dropdownArgs? '}';
dropdownArgs: dropdownArg (',' dropdownArg)*;
dropdownArg: typeName ':' '{' dropdownKeysAndValues? '}';
dropdownKeysAndValues: dropdownKeyAndValue (',' dropdownKeyAndValue)*;
dropdownKeyAndValue: literal ':' literal;

form: 'form' ':' '{' (groups | fields)? '}';
groups: 'groups' ':' '{' groupArgs? '}';
groupArgs: group (',' group)*;
group: '{' id (fields | groups)? '}';

fields: 'fields' ':' '{' fieldArgs? '}';
fieldArgs: field (',' field)*;
field: RequiredType scalarType typeName ':' '{' dropdown? defaultValue? cssClass? '}';

dropdown: dropdownType 'dropdown' ':' typeName;
defaultValue: 'defaultValue' ':' literal;
cssClass: 'class' ':' '{' cssClassArgs? '}';
cssClassArgs: cssIdentifier (',' cssIdentifier)*;

statements: 'statements' ':' '{' statementsArgs? '}';
statementsArgs: statement (',' statement)*;
statement: typeName ':' '{' when then '}';

when: 'when' ':' StringLiteral;
then: 'then' ':' statementType message?;
statementType: StatementType;
message: 'message' ':' StringLiteral;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;