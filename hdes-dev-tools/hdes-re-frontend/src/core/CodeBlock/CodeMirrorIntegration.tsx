import React from 'react';

import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import {EditorState, EditorView, basicSetup} from "@codemirror/next/basic-setup"

import {javascript} from "@codemirror/next/lang-javascript"
import {oneDark} from "@codemirror/next/theme-one-dark"


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      height: '100%',
    },
  }),
);


interface CodeMirrorIntegrationProps {
  src: string
};

const CodeMirrorIntegration: React.FC<CodeMirrorIntegrationProps> = ({src}) => {
  const classes = useStyles();
  const ref = React.createRef<HTMLDivElement>();
  const [editor, setEditor] = React.useState<EditorView>();


  React.useEffect(() => {
    if(!editor) {
      
      
      console.log(basicSetup)
      const state = EditorState.create({doc: src, extensions: [
        basicSetup,
        javascript(),
        oneDark,
        EditorView.baseTheme({
          "$$focused": { outline: "unset" },
        })
      //  linter(esLint(new Linter)),
      //  StreamLanguage.define(javascript),
      ]})
      
      const instance: EditorView = new EditorView({state, parent: ref.current as Element})
      setEditor(instance);
    }
  }, [ref])

  return (<div ref={ref} className={classes.root}></div>);
}

export default CodeMirrorIntegration;
