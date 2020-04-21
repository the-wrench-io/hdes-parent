parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


dropdownType: DropdownType;
mtBody: id description? mtInputs dropdowns actions form EOF;
mtInputs: 'inputs' ':' typeDefs;

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

actions: 'actions' ':' '{' actionsArgs? '}';
actionsArgs: action (',' action)*;
action: typeName ':' '{' actionBodyWhen actionBodyThen '}';

actionBodyWhen: 'when' ':' StringLiteral;
actionBodyThen: 'then' ':' actionType message?;
actionType: StatementType;
message: 'message' ':' StringLiteral;
