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
import Immutable from 'immutable'

const ID = 'iconbar'
const init = {
  explorer: { enabled: false },
  search: { enabled: false },
  debug: { enabled: false },
  changes: { enabled: false },
  create: { enabled: false },
  delete: { enabled: false },
};

const toggle = (model, name) => {
  const currentValue = model.getIn([ID, name, 'enabled']);
  return model
    .setIn([ID, 'explorer', 'enabled'], false)
    .setIn([ID, 'search', 'enabled'], false)
    .setIn([ID, 'debug', 'enabled'], false)
    .setIn([ID, 'changes', 'enabled'], false)
    .setIn([ID, 'create', 'enabled'], false)
    .setIn([ID, 'delete', 'enabled'], false)
    .setIn([ID, name, 'enabled'], !currentValue);
}

// all explorer actions
const actions = ({actions, update}) => ({
  toggleExplorer: () => {
    update(model => toggle(model, 'explorer'))
  },
  toggleSearch: () => {
    update(model => toggle(model, 'search'))
  },
  toggleDebug: () => {
    update(model => toggle(model, 'debug'))
  },
  toggleChanges: () => {
    update(model => toggle(model, 'changes'))
  },
  toggleNewitem: () => {
    update(model => toggle(model, 'create'))
  },
  toggleDelete: () => update(model => {
    const result = toggle(model, 'delete');
    const entry = Immutable.fromJS({type: 'delete', id: 'delete-view-entry'});

    if(result.getIn([ID, 'delete', 'enabled'])) {
      actions.editor.open(entry)
    } else {
      actions.editor.close(entry)
    }
    return result;


  })
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
