import { Component } from 'inferno';
import { Header } from './components/Header';
import { Inputs } from './components/Inputs';
import { Tasks } from './components/Tasks';

/*
<li><Line id={0} keyword={'id'} value='dshjgfsdhjgf' /></li>
<li><Line id={1} keyword={'description'} value='dshjgfsdhjgf' /></li>
<li><Line id={2} keyword={'tasks'} value='' /></li>
<li><a>Dashboard</a></li>
<li><a>Customers</a></li>

const ondragenter = function (e) { e.preventDefault(); this.className = 'nicenice lvl-over'; return false; };
const ondragleave = function () { this.className = 'nicenice'; return false; };
const ondragover = function (e) { e.preventDefault() }
const ondrop = function (e) {
  e.preventDefault();
  console.log("GOT DROP EVENT", e);
  return false;
};
*/

const toolbarItem = ({icon, tooltip, onClick}) => {
  return (<div class='columns is-centered'>
    <a href={'#toolbar'} class='has-tooltip-right' data-tooltip={tooltip} onClick={onClick}>
      <span class={`fas fa-lg ${icon} icon is-large`} />
    </a>
  </div>)
}

export class FlView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['editorfl']
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))
  }
  render() {
    const { actions, state } = this.props
    const active = state.getIn(['editorfl', 'active'])

    if(!active) {
      return <div>Loading...</div>
    }
    const model = state.getIn(['editorfl', 'models', active]).toJS()

    return (<div class='tile flow-editor'>
      <div class='tile is-child is-toolbar'>
        {toolbarItem({icon: 'fa-arrow-alt-circle-right'})}
        {toolbarItem({icon: 'fa-table'})}
        {toolbarItem({icon: 'fa-code'})}
        {toolbarItem({icon: 'fa-project-diagram'})}
        {toolbarItem({icon: 'fa-user'})}
        {toolbarItem({icon: 'fa-random'})}
      </div>

      <div class='tile is-child'>
          <aside class='menu'>
            <ul class='menu-list is-editor'>
              <Header actions={actions} state={state} view={model.view.value} />
              <Inputs actions={actions} state={state} view={model.view.value} />
              <Tasks actions={actions} state={state} view={model.view.value} />
            </ul>
          </aside>
      </div>
    </div>
    )
  }
}