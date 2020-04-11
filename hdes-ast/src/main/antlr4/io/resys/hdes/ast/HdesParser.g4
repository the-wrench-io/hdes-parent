parser grammar HdesParser;
options { tokenVocab = HdesLexer; }
import CommonParser;

compilationUnit: def EOF;

def: defDecisionTable? defManualTask? defServiceTask? defFlow?;

defFlow: 'def' 'flow' ':' '{' bodyArgs? '}';
defDecisionTable: 'def' 'decisionTable' ':' '{' bodyArgs? '}';
defManualTask: 'def' 'manualTask' ':' '{' bodyArgs? '}';
defServiceTask: 'def' 'service' ':' '{' bodyArgs? '}';

bodyArgs: body (',' body)*;
body: Characters?;