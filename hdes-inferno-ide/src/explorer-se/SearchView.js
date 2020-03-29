import { Component } from 'inferno';


const createSubSection = (actions, state, entry) => {
  const id = entry.get('id');
  const type = entry.get('type');
  const entryActive = state.getIn(['search', 'entryActive']);

  return entry.get('matches').map(m => {
    const section = id + '/' + m.get('id');
    const linkStyle = entryActive === section ? 'is-active' : null;
    return <li class='is-match'>
      <a class={linkStyle} href={'#' + type + '/' + section}
        onClick={() => {
          actions.search.toggleSearchEntrySection(id, section)
          actions.explorer.openEntry(id);
        }}>
        <div class='is-id'>found on: {m.get('id')}</div>
        <div>{m.get('value')}</div>
      </a>
    </li>
  }).toJS();
}

const createSection = (actions, state) => {

  const entriesCollapsed = state.getIn(['search', 'entriesCollapsed']);
  const entryActive = state.getIn(['search', 'entryActive']);

  const entries = state.getIn(['search', 'entries']).map(e => {
    const id = e.get('id');
    const type = e.get('type');
    const name = e.get('name');
    const isActive = entriesCollapsed.indexOf(id) < 0;
    
    const iconStyle = isActive ? 'la-angle-down' : 'la-angle-right';
    const linkStyle = entryActive === id ? 'is-active' : null;

    return [(<li class='is-entry'>
      <a href={'#' + type + '/' + id} class={linkStyle} 
        onClick={() => actions.search.toggleSearchEntry(id)} 
        onDblClick={() => actions.explorer.openEntry(id)}>
          
        <i class={'las ' + iconStyle + ' icon is-small'}/>
        {name}
        <span class="icon is-small is-type">{type}</span>
      </a>
    </li>), ...isActive ? createSubSection(actions, state, e) : []]
    }).toJS();

  const count = state.getIn(['search', 'count']);
  let summary;
  const countEntries = count.get('entries')
  if(countEntries === 0) {
    summary = (<li class='explorer-subtitle'>no results found</li>);
  } else if(countEntries > 0) {
    summary = (<li class='explorer-subtitle'>{count.get('matches')} results in {count.get('entries')} files</li>);
  } else {
    return null;
  }
  return [summary, ...entries]
}

export class SearchView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['search'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { actions, state } = this.props;
    return (
      <aside class='search'>
        <ul class="menu-list">
          <li class='explorer-title'>search</li>
          <div class="field">
            <div class='control'>
              <input onInput={e => actions.search.setSearchFilter(e.target.value)} 
                class="is-rounded input is-primary" 
                type="text" placeholder="Search..." />
            </div>
          </div>  
          {createSection(actions, state)}
        </ul>
      </aside>
    );
  }
}