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
import { Component } from 'inferno'


export class DebugView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key0 = ['debug']
    const key1 = ['explorer']
    return !this.props.state.getIn(key0).equals(nextProps.state.getIn(key0)) || 
      !this.props.state.getIn(key1).equals(nextProps.state.getIn(key1));
  }

  getDef(state) {
    const explorer = state.getIn(['explorer'])
    const entryOpen = explorer.get('entryOpen')
    if(!entryOpen) {
      return null
    }
    
    return explorer.get('entries').filter(e => e.get('id') === entryOpen).toJS()[0]
  }

  getDebugField(def, input, actions) {
    return <div class='field'>
      <label class='label explorer-subtitle'>{input.name}</label>
      <div class='control'>
        <input onInput={e => actions.debug.setInputFieldValue(def, input)}
          onClick={e => actions.debug.setInputFieldActive(def, input)}
          class='input is-primary' 
          type='text' placeholder={input.name} 
          defaultValue={input.debugValue.empty ? '' : input.debugValue} />
      </div>
    </div>
  }

  getDebugInput(def, actions) {
    if(!def.ast.inputs.length) {
      return <div class='notification is-primary'>Resource has no inputs!</div>
    }
    const inputs = def.ast.inputs.map(input => this.getDebugField(def, input, actions))
    const runButton = <div class='field control'><button class='button is-fullwidth' onClick={() => actions.debug.run()}>Run and show summary</button></div>
    return [runButton, ...inputs]
  }

  render() {
    const { actions, state } = this.props;
    const def = this.getDef(state)
    const debug = def ? this.getDebugInput(def, actions) : <div class='notification is-primary'>No resources open to debug!</div>
    return (
      <aside class='debug'>
        <ul class='menu-list'>
          <li class='explorer-title'>debug</li>
          {debug}
        </ul>
      </aside>
    );
  }
}
