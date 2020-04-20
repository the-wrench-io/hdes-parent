parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


dropdownType: DropdownType;
mt: id description? inputs dropdowns statements form EOF;

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
field: typeName scalarType RequiredType ':' '{' dropdown? defaultValue? cssClass? '}';

dropdown: dropdownType 'dropdown' ':' typeName;
defaultValue: 'defaultValue' ':' literal;
cssClass: 'class' ':' StringLiteral;

statements: 'statements' ':' '{' statementsArgs? '}';
statementsArgs: statement (',' statement)*;
statement: typeName ':' '{' when then '}';

when: 'when' ':' StringLiteral;
then: 'then' ':' statementType message?;
statementType: StatementType;
message: 'message' ':' StringLiteral;