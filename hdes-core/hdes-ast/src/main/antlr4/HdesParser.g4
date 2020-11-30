parser grammar HdesParser;
options { tokenVocab = HdesLexer; }
import DecisionTableParser, ExpressionParser, ServiceTaskParser, FlowParser;

hdesContent: hdesBody* EOF;
hdesBody: (
    'flow' flBody
  | 'decision-table' dtBody
  | 'service-task' stBody
  | 'expression' '{' enBody '}');