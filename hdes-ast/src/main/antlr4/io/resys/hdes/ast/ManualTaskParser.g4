parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

mt: id description? groups EOF;

groups: group (',' group)*;
group: typeName ':' (groups | fields);
fields: field (',' field)*;
field: typeName ':' props;
props: RequiredType ScalarType DropDownType? id description? cssClass?;
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