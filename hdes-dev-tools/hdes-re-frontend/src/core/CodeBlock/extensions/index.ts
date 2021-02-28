import { AppTheme } from '../../Themes';
import { EditorState } from "@codemirror/next/basic-setup";
import { highlightSpecialChars, drawSelection, highlightActiveLine, keymap } from '@codemirror/next/view';
import { history, historyKeymap } from '@codemirror/next/history';
import { foldGutter, foldKeymap } from '@codemirror/next/fold';
import { indentOnInput } from '@codemirror/next/language';
import { lineNumbers } from '@codemirror/next/gutter';
import { defaultKeymap } from '@codemirror/next/commands';
import { bracketMatching } from '@codemirror/next/matchbrackets';
import { closeBrackets, closeBracketsKeymap } from '@codemirror/next/closebrackets';
import { highlightSelectionMatches, searchKeymap } from '@codemirror/next/search';
import { autocompletion, completionKeymap } from '@codemirror/next/autocomplete';
import { commentKeymap } from '@codemirror/next/comment';
import { rectangularSelection } from '@codemirror/next/rectangular-selection';
import { lintKeymap } from '@codemirror/next/lint';

//import Linter from "eslint4b-prebuilt"
//import {linter} from "@codemirror/lint"

import { createLinter } from './lint-hdes';
import { typescript } from './lang-hdes';
import { hdesDark } from './theme-hdes-dark';

import { Backend } from '../../Resources/Backend'




const createExtensions = (service: Backend.Service, appTheme: AppTheme) => {
  return [
    lineNumbers(),
    highlightSpecialChars(),
    history(),
    foldGutter(),
    drawSelection(),
    EditorState.allowMultipleSelections.of(true),
    indentOnInput(),
    bracketMatching(),
    closeBrackets(),
    autocompletion(),
    rectangularSelection(),
    highlightActiveLine(),
    highlightSelectionMatches(),
    keymap.of([
        ...closeBracketsKeymap,
        ...defaultKeymap,
        ...searchKeymap,
        ...historyKeymap,
        ...foldKeymap,
        ...commentKeymap,
        ...completionKeymap,
        ...lintKeymap
    ]),
    typescript(),
    createLinter(service),
    hdesDark(appTheme),
    // linter(esLint(new Linter())),
    // StreamLanguage.define(javascript),
  ]
}
export default createExtensions;
