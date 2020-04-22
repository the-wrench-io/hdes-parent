parser grammar FlowParser;
options { tokenVocab = HdesLexer; }
import CommonParser;

taskTypes
  : MANUAL_TASK
  | FLOW_TASK 
  | DT_TASK
  | ST_TASK;
objectDataType: OBJECT | ARRAY;

flBody: typeName description? headers tasks;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: nextTask (',' nextTask)*;
nextTask: typeName ':' '{' taskPointer taskRef? '}';

taskPointer: whenThenPointerArgs | thenPointer;
whenThenPointerArgs: whenThenPointer (',' whenThenPointer)*;
whenThenPointer: 'when' ':' ('?' | enBody) thenPointer;
thenPointer: 'then' ':' (endMapping | typeName);

taskRef: taskTypes ':' typeName mapping;  
endMapping: 'end' mapping;
mapping: 'mapping' objectDataType ':' '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;

