lexer grammar HdesLexer;
ReservedKeyWord: 'switch' | 'case' | 'if' | 'else';

ScalarType
  : INTEGER
  | DECIMAL
  | DATE_TIME
  | DATE
  | TIME
  | STRING
  | BOOLEAN;

// BETWEEN/AND/OR/IN operators
IN: I N;
BETWEEN: B E T W E E N;
AND: A N D;
OR: O R;
WHERE: W H E R E;

fragment A : [aA];
fragment B : [bB];
fragment D : [dD];
fragment E : [eE];
fragment H : [hH];
fragment I : [iI];
fragment N : [nN];
fragment O : [oO];
fragment R : [rR];
fragment T : [tT];
fragment W : [wW];


ACCEPTS: 'accepts';
RETURNS: 'returns';

// Main definition types
DEF_FL: 'flow';
DEF_DT: 'decision-table';
DEF_ST: 'service-task';
DEF_EN: 'expression';

// data type options
DEBUG_VALUE: 'debug-value';

// possible data types
INTEGER: 'INTEGER';
DECIMAL: 'DECIMAL';
DATE_TIME: 'DATE_TIME';
DATE: 'DATE';
TIME: 'TIME';
STRING: 'STRING';
BOOLEAN: 'BOOLEAN';
OBJECT: 'OBJECT';
ARRAY: 'ARRAY';
OF: 'of'; // ARRAY of STRING 

LAMBDA: '->';

// DT matching policy
ALL: 'ALL';
FIRST: 'FIRST';
MATCHES: 'matches';

// FLOW
STEPS: 'steps';
CALL: 'call';
AWAIT: 'await';
CONTINUE: 'continue';


// TODO replace with expression 
FIND_FIRST: 'find-first';
SORT_BY: 'sort-by';
ASC: 'ASC';
DESC: 'DESC';


// SERVICE
PROMISE: 'promise';
TIMEOUT: 'timeout';

WHEN: 'when';
THEN: 'then';
END: 'end-as';
AS: 'as';
MAPS: 'maps';
TO: 'to';

// MARKS
QUESTION_MARK: '?';
COLON: ':';
DOT: '.';
COMMA: ',';
NOT: '!';

PARENTHESES_START: '(';
PARENTHESES_END: ')';

// BLOCKS
BLOCK_START: '{';
BLOCK_END: '}';

// mathematical operators
ADD: '+';
SUBTRACT: '-';
MULTIPLY: '*';
DIVIDE: '/';

// equality operators
EQ_NOTEQUAL: '!=';
EQ_EQUAL: '=';
EQ_LESS: '<';
EQ_LESS_THEN: '<=';
EQ_GREATER: '>';
EQ_GREATER_THEN: '>=';

// integer literal
IntegerLiteral: '0' | NonZeroDigit (Digits? | Underscores Digits);
Placeholder: '_' Digits?;

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
