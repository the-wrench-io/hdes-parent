import { Component } from 'inferno'


export class SectionTabs extends Component {
  render() {
    const { state, actions, type, name } = this.props;
    const isOpen = state.getIn(['explorer', 'entriesOpen']).indexOf(type) > -1;
    const iconStyle = isOpen ? 'la-angle-down' : 'la-angle-right';
    //const linkStyle = isOpen ? 'is-active' : null;
  
    const sectionEntries = !isOpen ? [] : state.getIn(['explorer', 'entriesEditing']).toJS().map(id => {
    
      const e = state.getIn(['explorer', 'entries']).filter(item => item.get('id') === id).get(0);
      const isOpen = state.getIn(['explorer', 'entryOpen']) === id;
      const iconStyle = isOpen ? 'is-active' : null;
  
      return <li class="is-entry">
        <a href={'#' + type + '/' + id} onClick={() => actions.explorer.openEntry(id)} class={iconStyle}>{e.get('name') ? e.get('name') : '<no name>'}</a>
      </li>
    });

    return ([
      <li><a href={'#' + type} onClick={() => actions.explorer.toggleEntries(type)}><i class={'las ' + iconStyle + ' icon is-small'}/>{name}</a></li>,
      ...sectionEntries
    ])
  }
}