import React from 'react';

import { makeStyles, Theme, createStyles, useTheme } from '@material-ui/core/styles';
import { AppTheme } from '../Themes';
import {EditorState, EditorView, basicSetup} from "@codemirror/next/basic-setup"

import { javascript } from "@codemirror/next/lang-javascript"
import { hdesDark } from "./theme-hdes-dark"


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
  const theme: AppTheme = useTheme();

  React.useEffect(() => {
    if(!editor) {

      
      console.log(basicSetup)
      const state = EditorState.create({doc: src, extensions: [
        basicSetup,
        javascript(),
        hdesDark(theme),
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
