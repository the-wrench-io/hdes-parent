parser grammar FlowParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;

flowUnit: simpleTypeName '{' headers STEPS '{' steps '}' '}';
steps: step*;
step: simpleTypeName '{' (iterateAction | callAction ) pointer '}' stepAs?;
stepAs: AS mapping;

callAction: callDef*;
callDef: (CALL | AWAIT) simpleTypeName mapping;

iterateAction: MAP typeName TO '{' iterateBody '}';
iterateBody: callAction | steps;

pointer: whenThenPointerArgs | thenPointer;
whenThenPointerArgs: (whenThenPointer (whenThenPointer)* elsePointer?) | thenPointer?; 
whenThenPointer: WHEN '(' expressionUnit ')' thenPointer;
elsePointer: ELSE thenPointer;

thenPointer: (RETURN (endAsPointer | simpleTypeName)) | continuePointer;
continuePointer: CONTINUE;
endAsPointer: mapping;