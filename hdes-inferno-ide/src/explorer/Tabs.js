import { Component } from 'inferno';

const closeIcon = <i class="las la-times" />

export class Tabs extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['explorer'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { actions, state } = this.props;
    const entries = state.getIn(['explorer', 'entriesEditing']).toJS().map(id => {

      const isOpen = state.getIn(['explorer', 'entryOpen']) === id;
      const entryStyle = isOpen ? 'is-active' : null;
      const entry = state.getIn(['explorer', 'entries']).filter(e => e.get('id') === id).get(0);

      // Entry is not present anymore
      if(!entry) {
        return null;
      }
      const openEntry = () => actions.explorer.openEntry(id);
      const type = entry.get('type')

      return (<li class={entryStyle}><a href={'#entry/' + id} onClick={openEntry} >
          <span class='icon is-small is-type'>{type}</span>
          <span>{entry.get('name')}</span>
          <button class='delete is-close' onClick={() => actions.explorer.closeEntry(id)} >{closeIcon}</button>
        </a></li>);
    });

    return (<div class='tabs is-boxed editor-tb'><ul>{entries}</ul></div>);
  }
}