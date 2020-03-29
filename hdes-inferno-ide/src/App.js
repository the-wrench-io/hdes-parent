import { Component } from 'inferno';

import { Tabs } from './explorer';
import { EditorView } from './editor';
import { Iconbar, IconbarView } from './iconbar';
import { Init, Healthbar } from './health';


export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = props.states();
    this.skippedFirst = false;
  }
  componentDidMount() {
    var setState = this.setState.bind(this);
    this.props.states.map(state => 
      this.skippedFirst ? setState(state) : this.skippedFirst = true);
  }

  render() {
    const state = this.state
    const { actions } = this.props

    // application init
    if(state.getIn(['health', 'init', 'enabled'])) {
      return (<div class='app'>
        <Init state={state} actions={actions} />
      </div>)
    }

    return (<div class="tile is-ancestor is-radiusless is-marginless is-paddingless app">
      <div class="tile">
        <div class="tile is-parent is-vertical is-radiusless is-marginless is-paddingless">
          <div class="tile is-parent is-radiusless is-marginless is-paddingless">
            <Iconbar state={state} actions={actions}/>
            <IconbarView state={state} actions={actions}/>

            <div class="tile is-parent is-radiusless is-marginless is-paddingless editor-window">
              <div class="tile is-child">
                <Tabs state={state} actions={actions}/>
                <EditorView state={state} actions={actions} />
              </div>
            </div>
          </div>

          <div class="tile is-parent is-radiusless is-marginless is-paddingless is-ide-info">
            <div class="tile is-child">
              <Healthbar state={state} actions={actions}/>
            </div>
          </div>

        </div>
      </div>
    </div>)
  }
}
