parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

mt: id description? inputs groups EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

groups: group (',' group)*;
group: typeName ':' '{' (groups | fields) '}';

fields: field (',' field)*;
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
input: RequiredType DataType typeName;
defaultValue: 'defaultValue' ':' literal;