import { Component } from 'inferno'


const active = (state, id) => {
  return state.getIn(['iconbar', id, 'enabled'])
}

const Icon = ({active, icon, onClick}) => {
  const color = active ? 'active' : '';
  const style = 'button is-fullwidth ' + color
  return <div class={style} onClick={onClick}><span class={`${icon} is-size-4 icon`} /></div>
}

export class Iconbar extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['iconbar'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { actions, state } = this.props
    return (<div class='tile is-parent is-radiusless is-marginless is-paddingless iconbar'>
      <div class='tile is-child'>
        <Icon active={active(state, 'explorer')} icon='icon-explorer' onClick={actions.iconbar.toggleExplorer} />
        <Icon active={active(state, 'search')} icon='icon-search' onClick={actions.iconbar.toggleSearch} />
        <Icon active={active(state, 'debug')} icon='icon-debug' onClick={actions.iconbar.toggleDebug} />
        <Icon active={active(state, 'changes')} icon='icon-changes' onClick={actions.iconbar.toggleChanges} />
        <Icon active={active(state, 'create')} icon='icon-create' onClick={actions.iconbar.toggleNewitem} />
        <Icon active={active(state, 'delete')} icon='icon-delete' onClick={actions.iconbar.toggleDelete} />
      </div>
    </div>);
  }
}