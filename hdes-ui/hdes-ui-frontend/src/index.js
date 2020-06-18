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
import { render } from 'inferno';
import Immutable from 'immutable';
import flyd from 'flyd';
import * as serviceWorker from './serviceWorker';

import './index.css';
import './App.scss';
import App from './App';

import { createBackendState } from './core-backend';
import { createSupportbarState } from './supportbar';
import { createIconbarState } from './iconbar';
import { createHealthState } from './health';

import { createExplorerState } from './explorer';
import { createSearchState } from './explorer-se';
import { createCreateState } from './explorer-cr';
import { createDebugState } from './explorer-dg';
import { createEditorState } from './editor';
import { createEditorFlState } from './editor-fl';
import { createEditorDlState } from './editor-dl';
import { createEditorDtState } from './editor-dt';
import { createEditorTxState } from './editor-tx';


class Store {
  constructor(config, createComponentStates) {
    this.update = flyd.stream()
    this.config = config
    this.actions = {}
    this.initActions = []
    this.currentState = {}
    this.states = undefined
    this.addComponents(createComponentStates).finalize()
  }

  addComponent(createComponentState) {
    const componentState = createComponentState(this)
    const { id, initial, actions } = componentState

    if(this.actions[id]) {
      throw Error(`Store has already registered component with id: ${id}!`)
    }

    this.actions[id] = actions
    this.currentState[id] = initial

    if(actions.init) {
      this.initActions.push(actions.init)
    }

    return this;
  }

  addComponents(createComponentStates) {
    createComponentStates.forEach(c => this.addComponent(c))
    return this;
  }

  finalize() {
    const setCurrentState = (value) => this.currentState = value;

    const merge = (currentState, updateCommand) => {
      const result = updateCommand(currentState);
      if(result) {
        setCurrentState(result)
        return result;
      }
      return currentState;
    }

    setCurrentState(Immutable.fromJS(this.currentState))
    this.states = flyd.scan(merge, this.currentState, this.update)
    this.initActions.forEach(i => i())
    return this;
  }
}

const config = Immutable
  .fromJS(window.config ? window.config : 
    {
      url: 'http://localhost:8080/hdes-ui/services'
    });

const store = new Store(config, [
  createBackendState(config),
  createHealthState,
  createSearchState,
  createExplorerState,
  createDebugState,
  createIconbarState,
  createSupportbarState,
  createCreateState,
  createEditorState,
  createEditorFlState,
  createEditorDlState,
  createEditorDtState,
  createEditorTxState])

render(<App states={store.states} actions={store.actions} />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();