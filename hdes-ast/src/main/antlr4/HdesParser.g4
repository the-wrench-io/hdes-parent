parser grammar HdesParser;
options { tokenVocab = HdesLexer; }
import DecisionTableParser, ExpressionParser, FlowParser, ManualTaskParser;


hdesBodyType
  : DEF_FL ':' flBody
  | DEF_DT ':'dtBody
  | DEF_MT ':' mtBody
  | DEF_EN ':' enBody;
hdesBody: 'define' hdesBodyType;
