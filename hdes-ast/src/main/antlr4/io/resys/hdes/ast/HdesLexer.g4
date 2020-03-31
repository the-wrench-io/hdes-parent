lexer grammar HdesLexer;


ObjectDataType: OBJECT | ARRAY;

DataType
  : ScalarTypes
  | ObjectDataType;

ScalarTypes
  : INTEGER
  | DECIMAL
  | DATE_TIME
  | STRING
  | BOOLEAN;

TaskType
  : MANUAL_TASK
  | FLOW_TASK 
  | DT_TASK
  | ST_TASK;

MANUAL_TASK: 'manualTask';
FLOW_TASK: 'flowTask';
DT_TASK: 'decisionTask';
ST_TASK: 'serviceTask';

INTEGER: 'INTEGER';
DECIMAL: 'DECIMAL';
DATE_TIME: 'DATE_TIME';
DATE: 'DATE';
STRING: 'STRING';
BOOLEAN: 'BOOLEAN';
OBJECT: 'OBJECT';
ARRAY: 'ARRAY';
TIME: 'TIME';


COLON: ':';
SEMICOLON: ';';
DOT: '.';

ID: 'id';
DESC: 'description';
INPUTS: 'inputs';
TASKS: 'tasks';
DEBUG_VALUE: 'debugValue';
REQUIRED: 'required';
MAPPING: 'mapping';
WHEN: 'when';
THEN: 'then';

BLOCK_START: '{';
BLOCK_END: '}';


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
fragment Letters: [a-zA-Z$_];
fragment LettersAndDigits: [a-zA-Z0-9$_];


// comments and white spaces
WHITE_SPACE : [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT_BLOCK : '/*' .*? '*/' -> channel(HIDDEN);
COMMENT_LINE : '//' ~[\r\n]* -> channel(HIDDEN);