parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;
dataType
  : ObjectDataType
  | ScalarType;
  
mt: id description? inputs groups EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

groups: 'groups' ':' '{' groupArgs? '}';
groupArgs: group (',' group)*;
group: typeName ':' '{' (groups | fields)? '}';

fields: 'fields' ':' '{' fieldArgs? '}';
fieldArgs: field (',' field)*;
field: typeName ':' '{' props '}';
props: input DropDownType? defaultValue? cssClass?;
cssClass: 'class' ':' '{' cssClassArgs? '}';
cssClassArgs: CssIdentifier (',' CssIdentifier)*;

statements: 'statements' ':' '{' statement (',' statement)* '}';
statement: typeName ':' '{' when then message '}';

when: 'when' ':' StringLiteral;
then: 'then' ':' StatementType;
message: 'message' ':' StringLiteral;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
input: RequiredType dataType typeName;
defaultValue: 'defaultValue' ':' literal;