parser grammar ManualTaskParser;
options { tokenVocab = HdesLexer; }
import CommonParser;


dropdownType: DropdownType;
mtBody: typeName description? headers dropdowns actions form;

dropdowns: 'dropdowns' ':' '{' dropdownArgs? '}';
dropdownArgs: dropdownArg (',' dropdownArg)*;
dropdownArg: typeName ':' '{' dropdownKeysAndValues? '}';
dropdownKeysAndValues: dropdownKeyAndValue (',' dropdownKeyAndValue)*;
dropdownKeyAndValue: literal ':' literal;

form: 'form' 'of' (groups | fields)? from;
from: 'from' (typeName | '?');
groups: 'groups' ':' '{' groupArgs? '}';
groupArgs: group (',' group)*;
group: typeName ':' '{' (fields | groups)? '}';

fields: 'fields' ':' '{' fieldArgs? '}';
fieldArgs: field (',' field)*;
field: typeName scalarType RequiredType ':' '{' dropdown? defaultValue? cssClass? '}';

dropdown: dropdownType 'dropdown' ':' typeName;
defaultValue: 'default-value' ':' literal;
cssClass: 'class' ':' StringLiteral;

actions: 'actions' ':' '{' actionsArgs? '}';
actionsArgs: action (',' action)*;
action: actionBodyWhen actionBodyThen;

actionBodyWhen: 'when' typeName ':' enBody;
actionBodyThen: 'then' actionType;
actionType: 'show' (showMessage | showGroupOrField);

showGroupOrField: ('group' | 'field') ':' typeName;
showMessage: ('error' | 'info' | 'warning') 'message' ':' StringLiteral;
