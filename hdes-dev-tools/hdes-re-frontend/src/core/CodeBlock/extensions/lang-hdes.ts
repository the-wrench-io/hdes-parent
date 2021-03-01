/* eslint-disable no-template-curly-in-string */
import { parser } from '../lezer-grammar';

import { LezerLanguage, indentNodeProp, continuedIndent, flatIndent, delimitedIndent, foldNodeProp, LanguageSupport } from '@codemirror/next/language';
import { styleTags, tags } from '@codemirror/next/highlight';
import { snippetCompletion, ifNotIn, completeFromList } from '@codemirror/next/autocomplete';

/// A collection of JavaScript-related
/// [snippets](#autocomplete.snippet).
const snippets = [
    snippetCompletion("function ${name}(${params}) {\n\t${}\n}", {
        label: "function",
        detail: "definition",
        type: "keyword"
    }),
    snippetCompletion("for (let ${index} = 0; ${index} < ${bound}; ${index}++) {\n\t${}\n}", {
        label: "for",
        detail: "loop",
        type: "keyword"
    }),
    snippetCompletion("for (let ${name} of ${collection}) {\n\t${}\n}", {
        label: "for",
        detail: "of loop",
        type: "keyword"
    }),
    snippetCompletion("try {\n\t${}\n} catch (${error}) {\n\t${}\n}", {
        label: "try",
        detail: "block",
        type: "keyword"
    }),
    snippetCompletion("class ${name} {\n\tconstructor(${params}) {\n\t\t${}\n\t}\n}", {
        label: "class",
        detail: "definition",
        type: "keyword"
    }),
    snippetCompletion("import {${names}} from \"${module}\"\n${}", {
        label: "import",
        detail: "named",
        type: "keyword"
    }),
    snippetCompletion("import ${name} from \"${module}\"\n${}", {
        label: "import",
        detail: "default",
        type: "keyword"
    })
];

/// A language provider based on the [Lezer JavaScript
/// parser](https://github.com/lezer-parser/javascript), extended with
/// highlighting and indentation information.
const javascriptLanguage = LezerLanguage.define({
    parser: parser.configure({
        props: [
            indentNodeProp.add({
                IfStatement: continuedIndent({ except: /^\s*({|else\b)/ }),
                TryStatement: continuedIndent({ except: /^\s*({|catch|finally)\b/ }),
                LabeledStatement: flatIndent,
                SwitchBody: context => {
                    let after = context.textAfter, closed = /^\s*\}/.test(after), isCase = /^\s*(case|default)\b/.test(after);
                    return context.baseIndent + (closed ? 0 : isCase ? 1 : 2) * context.unit;
                },
                Block: delimitedIndent({ closing: "}" }),
                "TemplateString BlockComment": () => -1,
                "Statement Property": continuedIndent({ except: /^{/ })
            }),
            foldNodeProp.add({
                "Block ClassBody SwitchBody EnumBody ObjectExpression ArrayExpression"(tree) {
                    return { from: tree.from + 1, to: tree.to - 1 };
                },
                BlockComment(tree) { return { from: tree.from + 2, to: tree.to - 2 }; }
            }),
            styleTags({
                "get set async static": tags.modifier,
                "for while do if else switch try catch finally return throw break continue default case": tags.controlKeyword,
                "in of await yield void typeof delete instanceof": tags.operatorKeyword,
                "flow service decisiontable": tags.definitionKeyword,
                "export import let var const function class extends": tags.definitionKeyword,
                "with debugger from as new": tags.keyword,
                TemplateString: tags.special(tags.string),
                Super: tags.atom,
                BooleanLiteral: tags.bool,
                this: tags.self,
                null: tags.null,
                Star: tags.modifier,
                VariableName: tags.variableName,
                "CallExpression/VariableName": tags.function(tags.variableName),
                VariableDefinition: tags.definition(tags.variableName),
                Label: tags.labelName,
                PropertyName: tags.propertyName,
                "CallExpression/MemberExpression/PropertyName": tags.function(tags.propertyName),
                PropertyNameDefinition: tags.definition(tags.propertyName),
                UpdateOp: tags.updateOperator,
                LineComment: tags.lineComment,
                BlockComment: tags.blockComment,
                Number: tags.number,
                String: tags.string,
                ArithOp: tags.arithmeticOperator,
                LogicOp: tags.logicOperator,
                BitOp: tags.bitwiseOperator,
                CompareOp: tags.compareOperator,
                RegExp: tags.regexp,
                Equals: tags.definitionOperator,
                "Arrow : Spread": tags.punctuation,
                "( )": tags.paren,
                "[ ]": tags.squareBracket,
                "{ }": tags.brace,
                ".": tags.derefOperator,
                ", ;": tags.separator,
                TypeName: tags.typeName,
                TypeDefinition: tags.definition(tags.typeName),
                "type enum interface implements namespace module declare": tags.definitionKeyword,
                "abstract global privacy readonly": tags.modifier,
                "is keyof unique infer": tags.operatorKeyword
            })
        ]
    }),
    languageData: {
        closeBrackets: { brackets: ["(", "[", "{", "'", '"', "`"] },
        commentTokens: { line: "//", block: { open: "/*", close: "*/" } },
        indentOnInput: /^\s*(?:case |default:|\{|\})$/,
        wordChars: "$"
    }
});
/// A language provider for TypeScript.
const typescriptLanguage = javascriptLanguage.configure({ dialect: "ts" });

/// JavaScript support. Includes [snippet](#lang-javascript.snippets)
/// completion.
function typescript() {
    let lang = typescriptLanguage;
    return new LanguageSupport(lang, javascriptLanguage.data.of({
        autocomplete: ifNotIn(["LineComment", "BlockComment", "String"], completeFromList(snippets))
    }));
}


export { typescript };
