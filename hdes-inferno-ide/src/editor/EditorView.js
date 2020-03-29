import { Component } from 'inferno';
import { FlView } from './../editor-fl';
import { DlView } from './../editor-dl';
import { DtView } from './../editor-dt';


const createComponent = (actions, state) => {
  const entry = state.getIn(['editor', 'entry']);
  
  if(!entry) {
    if(state.getIn(['iconbar', 'delete', 'enabled'])) {
      return <DlView actions={actions} state={state} />
    }

    return null;
  }
  const type = entry.get('type')
  if(type === 'fl') {
    return <FlView actions={actions} state={state} entry={entry} />
  } else if(type === 'delete') {
    return <DlView actions={actions} state={state} />
  } else if(type === 'dt') {
    return <DtView actions={actions} state={state} entry={entry} />
  }
  return (<div>coming soon!</div>)
}

export class EditorView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    //const key = ['editor']
    //return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))

    return true;
  }

  render() {
    const { actions, state } = this.props
    return createComponent(actions, state)
  }
}