parser grammar ExpressionParser;
options { tokenVocab = HdesLexer; }

literal
  : IntegerLiteral
  | DecimalLiteral
  | BooleanLiteral
  | StringLiteral;

placeholderRule: Placeholder;
placeholderTypeName: placeholderRule | simpleTypeName;

simpleTypeName: Identifier;
typeName: placeholderTypeName | typeName '.' placeholderTypeName;

methodName: simpleTypeName;

// method invocation
methodInvocation
  : methodName '(' methodArgs? ')'
  | typeName '.' methodName '(' methodArgs? ')' ('.' expression)?;

methodArgs: methodArg (',' methodArg)*;
methodArg: expression;

primary
  : literal
  | typeName
  | '(' expression ')'
  | methodInvocation;

// final output
enBody: expression;

// expressions
expression: conditionalExpression | primary | lambdaExpression;

// lambda
lambdaExpression: lambdaParameters '->' lambdaBody;
lambdaParameters: typeName | '(' typeName (',' typeName)* ')';
lambdaBody: primary; 

conditionalExpression
  : conditionalOrExpression
  | conditionalOrExpression IN '(' expression (',' expression)* ')'
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
  : unaryExpressionNotPlusMinus
  | '+' unaryExpression
  | '-' unaryExpression
  | primary;
  
unaryExpressionNotPlusMinus: '!' unaryExpression;


