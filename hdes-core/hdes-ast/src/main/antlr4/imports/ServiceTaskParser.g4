parser grammar ServiceTaskParser;
options { tokenVocab = HdesLexer; }
import TypeDefParser, ExpressionParser;


stBody: simpleTypeName '{' headers externalService '}';
promise: PROMISE '{' promiseTimeout? '}';
promiseTimeout: TIMEOUT ':' enBody;
externalService: promise? typeName mapping; 