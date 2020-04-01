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
  
mt: id description? inputs statements form EOF;

inputs: 'inputs' ':' '{' inputArgs? '}';
inputArgs: input (',' input)*;

form: 'form' ':' '{' (groups | fields)? '}';
groups: 'groups' ':' '{' groupArgs? '}';
groupArgs: group (',' group)*;
group: '{' id (fields | groups)? '}';

fields: 'fields' ':' '{' fieldArgs? '}';
fieldArgs: field (',' field)*;


field: typeName ':' '{' props '}';
props: input DropDownType? defaultValue? cssClass?;
cssClass: 'class' ':' '{' cssClassArgs? '}';
cssClassArgs: CssIdentifier (',' CssIdentifier)*;

statements: 'statements' ':' '{' statementsArgs? '}';
statementsArgs: statement (',' statement)*;
statement: typeName ':' '{' when then '}';

when: 'when' ':' StringLiteral;
then: 'then' ':' StatementType (('expression' ':' StringLiteral) | message)?;
message: 'message' ':' StringLiteral;

id: 'id' ':' typeName;
description: 'description' ':' literal;
typeName: Identifier | typeName '.' Identifier;
input: RequiredType dataType typeName;
defaultValue: 'defaultValue' ':' literal;