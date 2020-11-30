parser grammar FlowParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;

flBody: simpleTypeName '{' headers 'steps' '{' steps '}' '}';
steps: step*;
step: simpleTypeName '{' (iterateAction | callAction ) pointer '}';

callAction: callDef*;
callDef: ('call' | 'await') simpleTypeName mapping;

iterateAction: 'maps' typeName 'to' '{' iterateBody '}' where? sortBy? findFirst?;
iterateBody: callAction | steps;

sortBy: 'sort-by' '{' sortByArg* '}';
sortByArg: typeName ('ASC' | 'DESC')?;

findFirst: 'find-first';
where: WHERE enBody (AND enBody)*;

pointer: whenThenPointerArgs || thenPointer;
whenThenPointerArgs: whenThenPointer (whenThenPointer)* thenPointer?; 
whenThenPointer: 'when' '{' enBody '}' thenPointer;
thenPointer: 'then' (endAsPointer | continuePointer | simpleTypeName);
continuePointer: CONTINUE;
endAsPointer: 'end-as' mapping;