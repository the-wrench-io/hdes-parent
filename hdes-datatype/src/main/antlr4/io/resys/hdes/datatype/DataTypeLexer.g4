lexer grammar DataTypeLexer;

// Lexer rules start with capital letters

//
BETWEEN: B E T W E E N;
AND: A N D;
OR: O R;

// BETWEEN/AND/OR operators
fragment A : [aA];
fragment B : [bB];
fragment D : [dD];
fragment E : [eE];
fragment N : [nN];
fragment O : [oO];
fragment R : [rR];
fragment T : [tT];

fragment W : [wW];

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
StringLiteral: '"' Characters? '"' | '\'' Characters? '\'';
fragment Characters: Character+;
fragment Character: ~["\\] | Escape;

// things to escape
fragment Escape: '\\' [btnfr"'\\];

// separators
PARENTHESES_START: '(';
PARENTHESES_END: ')';
BRACKET_START: '[';
BRACKET_END: ']';
QUESTION_MARK: '?';
COLON: ':';
COMMA: ',';
DOT: '.';
NOT: '!';
TIME: 'T';

EQ_NOTEQUAL: '!=';
EQ_EQUAL: '=';
EQ_LESS: '<';
EQ_LESS_THEN: '<=';
EQ_GREATER: '>';
EQ_GREATER_THEN: '>=';

// mathematical operators
ADD: '+';
SUBTRACT: '-';
MULTIPLY: '*';
DIVIDE: '/';
INCREMENT: '++';
DECREMENT: '--';

// naming convention
Name: Letters LettersAndDigits*;
fragment Letters: [a-zA-Z$_];
fragment LettersAndDigits: [a-zA-Z0-9$_];

// comments and white spaces
WHITE_SPACE : [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT_BLOCK : '/*' .*? '*/' -> channel(HIDDEN);
COMMENT_LINE : '//' ~[\r\n]* -> channel(HIDDEN);

