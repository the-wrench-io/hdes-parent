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
  : staticMethodInvocation
  | mapperMethodInvocation 
  | mappingInvocation;

staticMethodInvocation: StaticMethod '(' methodArgs? ')';
mapperMethodInvocation: methodName '(' methodArgs? ')' ('.' typeInvocation)?;

typeInvocation: typeName | boundMethod | mappingInvocation;
mappingInvocation: typeName '.' boundMethod ('.' typeInvocation)?;
boundMethod: mapMethod | filterMethod | sortMethod;
mapMethod: MAP '(' lambdaExpression ')';
filterMethod: TM_FILTER '(' lambdaExpression ')';
sortMethod: TM_SORT '(' lambdaExpression ')';
findFirstMethod: FIND_FIRST '(' ')';


methodArgs: methodArg (',' methodArg)*;
methodArg: expression;


primary
  : literal
  | typeName
  | '(' expression ')'
  | methodInvocation;

// final output
expressionUnit: expression;

// expressions
expression: conditionalExpression | primary | lambdaExpression;

// lambda
lambdaExpression: lambdaParameters '->' lambdaBody;
lambdaParameters: typeName | '(' typeName (',' typeName)* ')';
lambdaBody: primary; 

conditionalExpression
  : conditionalOrExpression
  | conditionalOrExpression StaticMethod '(' expression (',' expression)* ')'
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


