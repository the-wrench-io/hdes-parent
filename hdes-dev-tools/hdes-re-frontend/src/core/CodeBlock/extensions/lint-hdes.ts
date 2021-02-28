import { EditorView } from '@codemirror/next/view';
import * as Lint from '@codemirror/next/lint';
import { Backend } from '../../Resources/Backend'


type Diagnostics = readonly Lint.Diagnostic[] | Promise<readonly Lint.Diagnostic[]>;



const createLinter = (service: Backend.Service) => Lint.linter((view: EditorView): Diagnostics  => {
  console.log("linting");
  return [];
});


/*
source: fn(view: EditorView) → readonly Diagnostic[] | Promise<readonly Diagnostic[]>
) → Extension


eslint 

function mapPos(line, col, doc, offset) {
    return doc.line(line + offset.line).from + col + (line == 1 ? offset.col - 1 : -1);
}
function translateDiagnostic(input, doc, offset) {
    let start = mapPos(input.line, input.column, doc, offset);
    let result = {
        from: start,
        to: input.endLine != null && input.endColumn != 1 ? mapPos(input.endLine, input.endColumn, doc, offset) : start,
        message: input.message,
        source: input.ruleId ? "jshint:" + input.ruleId : "jshint",
        severity: input.severity == 1 ? "warning" : "error",
    };
    if (input.fix) {
        let { range, text } = input.fix, from = range[0] + offset.pos - start, to = range[1] + offset.pos - start;
        result.actions = [{
            name: "fix",
            apply(view, start) {
                view.dispatch({ changes: { from: start + from, to: start + to, insert: text }, scrollIntoView: true });
            }
        }];
    }
    return result;
}
*/

export { createLinter };