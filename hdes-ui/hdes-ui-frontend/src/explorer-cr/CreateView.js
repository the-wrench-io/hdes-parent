/*-
 * #%L
 * hdes-dev-app-ui
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

              <div><button class='button is-fullwidth' onClick={() => actions.create.create('FL', value)}>flow</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('DT', value)}>decision table</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('ST', value)}>service task</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('MT', value)}>manual task</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('TG', value)}>tag</button></div>
              <div><button class='button is-fullwidth' onClick={() => actions.create.create('US', value)}>user</button></div>

              {errors}
            </div>
          </div>  
          
        </ul>
      </aside>
    );
  }
}
