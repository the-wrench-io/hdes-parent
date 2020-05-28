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
import { createIconbarState } from './iconbar';
import { createHealthState } from './health';

import { createExplorerState } from './explorer';
import { createSearchState } from './explorer-se';
import { createCreateState } from './explorer-cr';
import { createEditorState } from './editor';
import { createEditorFlState } from './editor-fl';
import { createEditorDlState } from './editor-dl';
import { createEditorDtState } from './editor-dt';



const combineState = (update, states) => {
  const result = { initial: {}, actions: {}, inits: [] };

  for(let createCommand of states) {
    const state = createCommand((command) => command(result))
    const newInitial = {}
    newInitial[state.id] = state.initial

    const newActions = {}
    newActions[state.id] = state.actions(update)
    const initAction = newActions[state.id].init;

    if(initAction) {
      result.inits.push(initAction)
    }

    result.initial = Object.assign(result.initial, newInitial)
    result.actions = Object.assign(result.actions, newActions)
  }

  return result;
}

const merge = (currentState, updateCommand) => {
  const result = updateCommand(currentState);
  if(result) {
    return result; 
  }
  return currentState;
}


const config = Immutable
  .fromJS(window.config ? window.config : 
    {
      url: 'http://localhost:8080/hdes-ui/services'
    });

const update = flyd.stream()
const appState = combineState(update, [
  createBackendState(config),
  createHealthState,
  createSearchState,
  createExplorerState,
  createIconbarState,
  createCreateState,
  createEditorState,
  createEditorFlState,
  createEditorDlState,
  createEditorDtState])
const states = flyd.scan(merge, Immutable.fromJS(appState.initial), update)
const actions = appState.actions

// trigger inits
appState.inits.forEach(i => i())

render(<App states={states} actions={actions} />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
