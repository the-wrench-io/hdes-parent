import React from 'react';

import { makeStyles, Theme, createStyles, useTheme } from '@material-ui/core/styles';
import { AppTheme } from '../Themes';
import { EditorState, EditorView } from "@codemirror/next/basic-setup";
import { Resources } from '../Resources'
import createExtensions from './extensions'

////https://codepen.io/Hatcha/pen/JEJewj

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: '100%',
      height: '100%',
    },
  }),
);

interface CodeMirrorIntegrationProps {
  doc: string
};

const CodeMirror: React.FC<CodeMirrorIntegrationProps> = ({doc}) => {
  const classes = useStyles();
  const ref = React.createRef<HTMLDivElement>();
  const { service } = React.useContext(Resources.Context);
  
  const [editor, setEditor] = React.useState<EditorView>();
  const theme: AppTheme = useTheme();

  React.useEffect(() => {
    if(!editor) {
      const extensions =  createExtensions(service, theme);
      const state = EditorState.create({ doc, extensions })
      const instance: EditorView = new EditorView({state, parent: ref.current as Element})
      setEditor(instance);
    }
  }, [ref, service, theme, doc, editor])

  return (<div ref={ref} className={classes.root}></div>);
}

export default CodeMirror;
