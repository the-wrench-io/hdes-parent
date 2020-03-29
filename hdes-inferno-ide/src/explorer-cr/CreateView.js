import { Component } from 'inferno';


export class CreateView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['create'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { actions, state } = this.props
    const value = state.getIn(['create', 'value'])
    const errors =  state.getIn(['create', 'errors']).map(e => (
      <div class='notification is-danger'>
        <button class='delete' onClick={() => actions.create.deleteError(e.get('id'))}></button>
        {e.get('value')}
      </div>)).toJS()

    return (
      <aside class='explorer'>
        <ul class='menu-list'>
          <li class='explorer-title'>create new type</li>
          <div class='field'>
            <div class='control'>
              <input onInput={e => actions.create.setTypeName(e.target.value)}
                defaultValue={value}
                class='is-rounded input is-primary' type='text' placeholder='Type name...' />

              <div><button class='button is-fullwidth' onClick={() => actions.create.create('fl', value)}>flow</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('dt', value)}>decision table</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('st', value)}>service task</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('mt', value)}>manual task</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('tg', value)}>tag</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('us', value)}>user</button></div>

              {errors}
            </div>
          </div>  
          
        </ul>
      </aside>
    );
  }
}