parser grammar FlowParser;
options { tokenVocab = HdesLexer; }
import CommonParser;

taskTypes
  : DEF_FL
  | DEF_DT
  | DEF_MT
  | DEF_SE;

flBody: typeName description? headers tasks;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: nextTask (',' nextTask)*;
nextTask: typeName ':' '{' taskPointer taskRef? '}' fromPointer?;

taskPointer: whenThenPointerArgs | thenPointer;
whenThenPointerArgs: whenThenPointer (',' whenThenPointer)*;
whenThenPointer: 'when' ':' ('?' | enBody) thenPointer;
thenPointer: 'then' ':' (endMapping | typeName);
fromPointer: 'from' typeName thenPointer;

taskRef: taskTypes ':' typeName 'uses' ':' mapping;
endMapping: 'end' 'as' ':' mapping;

mapping: '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

mappingArg: typeName ':' mappingValue;
mappingValue: mapping | typeName | literal;

