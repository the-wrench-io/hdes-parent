import { Component } from 'inferno'

import { ExplorerView } from './../explorer';
import { SearchView } from './../explorer-se';
import { CreateView } from './../explorer-cr';


export class IconbarView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    //const key = ['iconbar'];
    //return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));

    return true;
  }
  render() {
    const { actions, state } = this.props

    let view;
    if(state.getIn(['iconbar', 'explorer', 'enabled'])) {
      view = (<ExplorerView state={state} actions={actions}/>);
    } else if(state.getIn(['iconbar', 'search', 'enabled'])) {
      view = (<SearchView state={state} actions={actions}/>);
    } else if(state.getIn(['iconbar', 'create', 'enabled'])) {
      view = (<CreateView state={state} actions={actions}/>);
    } else {
      return null;
    }

    return (<div class="tile is-parent is-3 is-radiusless is-marginless is-paddingless">
      <div class="tile is-child">{view}</div>
    </div>);
  }
}