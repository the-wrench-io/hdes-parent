parser grammar DataTypeParser;
options { tokenVocab = DataTypeLexer; }

// literals
literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

typeName : Name | typeName '.' Name;
methodName: Name;

// method invocation
methodInvocation
  : methodName '(' args? ')'
  | typeName '.' methodName '(' args? ')' ('.' expression)?;
args: expression (',' expression)*;

primary
  : literal
  | typeName
  | '(' expression ')'
  | methodInvocation;

// final output
compilationUnit: expression EOF;

// expressions
expression: conditionalExpression | primary;

conditionalExpression
  : conditionalOrExpression
  | conditionalOrExpression BETWEEN expression AND conditionalExpression
  | conditionalOrExpression '?' expression ':' conditionalExpression; 

conditionalOrExpression
  : conditionalAndExpression
  | conditionalOrExpression OR conditionalAndExpression;

conditionalAndExpression
  : andExpression
  | conditionalAndExpression AND conditionalOrExpression;

andExpression
  : equalityExpression
  | andExpression AND equalityExpression;

equalityExpression
  : relationalExpression
  | equalityExpression '=' relationalExpression
  | equalityExpression '!=' relationalExpression;

relationalExpression
  : additiveExpression
  | relationalExpression '<' additiveExpression
  | relationalExpression '<=' additiveExpression
  | relationalExpression '>' additiveExpression
  | relationalExpression '>=' additiveExpression;

additiveExpression
  : multiplicativeExpression
  | additiveExpression '+' multiplicativeExpression
  | additiveExpression '-' multiplicativeExpression;

multiplicativeExpression
  : unaryExpression
  | multiplicativeExpression '*' unaryExpression
  | multiplicativeExpression '/' unaryExpression;

// unary operation is an operation with only one operand
unaryExpression
  : preIncrementExpression
  | preDecrementExpression
  | unaryExpressionNotPlusMinus
  | '+' unaryExpression
  | '-' unaryExpression
  | primary;
  
preIncrementExpression: '++' unaryExpression;
preDecrementExpression: '--' unaryExpression;
unaryExpressionNotPlusMinus: postfixExpression | '!' unaryExpression;
postfixExpression: typeName ('++' | '--')*;


