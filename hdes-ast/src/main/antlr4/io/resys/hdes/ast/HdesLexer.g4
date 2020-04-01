lexer grammar HdesLexer;

ObjectDataType: OBJECT | ARRAY;
DirectionType: IN | OUT;
HitPolicyType: ALL | FIRST;
RequiredType: REQUIRED | OPTIONAL;
DropDownType: DROP_DOWN_SINGLE | DROP_DOWN_MULTIPLE; 
StatementType: SHOW | HIDE | ALERT;

DataType
  : ScalarType
  | ObjectDataType;
ScalarType
  : INTEGER
  | DECIMAL
  | DATE_TIME
  | DATE
  | TIME
  | STRING
  | BOOLEAN;
TaskType
  : MANUAL_TASK
  | FLOW_TASK 
  | DT_TASK
  | ST_TASK;

// BETWEEN/AND/OR operators
BETWEEN: B E T W E E N;
AND: A N D;
OR: O R;
fragment A : [aA];
fragment B : [bB];
fragment D : [dD];
fragment E : [eE];
fragment N : [nN];
fragment O : [oO];
fragment R : [rR];
fragment T : [tT];
fragment W : [wW];
  

MANUAL_TASK: 'manualTask';
FLOW_TASK: 'flowTask';
DT_TASK: 'decisionTask';
ST_TASK: 'serviceTask';

INTEGER: 'INTEGER';
DECIMAL: 'DECIMAL';
DATE_TIME: 'DATE_TIME';
DATE: 'DATE';
TIME: 'TIME';
STRING: 'STRING';
BOOLEAN: 'BOOLEAN';
OBJECT: 'OBJECT';
ARRAY: 'ARRAY';

ID: 'id';
DESC: 'description';

// DT
HEADERS: 'headers';
VALUES: 'values';
ALL: 'ALL';
FIRST: 'FIRST';
IN: 'IN';
OUT: 'OUT';

// MANUAL TASK
CLASS: 'class';
DROP_DOWN_SINGLE: 'selectOne';
DROP_DOWN_MULTIPLE: 'selectMultiple'; 
SHOW: 'SHOW';
HIDE: 'HIDE';
ALERT: 'ALERT';
STATEMENTS: 'statements';
MESSAGE: 'message';
DEFAULT_VALUE: 'defaultValue';

// FLOW
INPUTS: 'inputs';
TASKS: 'tasks';
DEBUG_VALUE: 'debugValue';
REQUIRED: 'required';
OPTIONAL: 'optional';
MAPPING: 'mapping';
WHEN: 'when';
THEN: 'then';
END: 'END';

// MARKS
QUESTION_MARK: '?';
COLON: ':';
DOT: '.';
COMMA: ',';
NOT: '!';

// BLOCKS
PARENTHESES_START: '(';
PARENTHESES_END: ')';
BLOCK_START: '{';
BLOCK_END: '}';

// mathematical operators
ADD: '+';
SUBTRACT: '-';
MULTIPLY: '*';
DIVIDE: '/';
INCREMENT: '++';
DECREMENT: '--';

// equality operators
EQ_NOTEQUAL: '!=';
EQ_EQUAL: '=';
EQ_LESS: '<';
EQ_LESS_THEN: '<=';
EQ_GREATER: '>';
EQ_GREATER_THEN: '>=';

// integer literal
IntegerLiteral: '0' | NonZeroDigit (Digits? | Underscores Digits);

fragment Digit : '0' | NonZeroDigit;
fragment NonZeroDigit: [1-9];
fragment Digits: Digit (DigitsAndUnderscores? Digit)?;
fragment DigitsAndUnderscores: DigitOrUnderscore+;
fragment DigitOrUnderscore: Digit | '_';
fragment Underscores: '_'+;

// decimal literal
DecimalLiteral: Digits '.' Digits? | '.' Digits | Digits;
fragment SignedInteger: Sign? Digits;
fragment Sign: [+-];

// boolean literals
BooleanLiteral: 'true' | 'false' ;

// string literal
StringLiteral: '\'' Characters? '\'';
fragment Characters: Character+;
fragment Character: ~['\\] | Escape;

// things to escape
fragment Escape: '\\' [btnfr"'\\];

// naming convention
Identifier: Letters LettersAndDigits*;
CssIdentifier: Letters LettersAndDigits* Dash*;
fragment Letters: [a-zA-Z$_];
fragment LettersAndDigits: [a-zA-Z0-9$_];
fragment Dash: '-'+;

// comments and white spaces
WHITE_SPACE : [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT_BLOCK : '/*' .*? '*/' -> channel(HIDDEN);
COMMENT_LINE : '//' ~[\r\n]* -> channel(HIDDEN);