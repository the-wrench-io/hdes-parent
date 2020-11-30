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
const ID = 'editorfl'
const init = {
  models: {},
  active: undefined, //id
  menu: {
    active: undefined // no
  }
}

// all explorer actions
const actions = ({ actions, update }) => ({
  init: () => { },
  load: () => update(model => {
    const entry = model.getIn(['editor', 'entry'])
    const id = entry.get('id');
    
    return model
      .updateIn([ID, 'models'], models => models.get(id) ? models : models.set(id, entry))
      .setIn([ID, 'active'], id);
  }),
  toggleActive: (id) => update(model => {
    const key = [ID, 'menu', 'active'];
    return model.getIn(key) === id ? model.deleteIn(key) : model.setIn(key, id)
  }),
  unsetActive: (id) => update(model => {
    const key = [ID, 'menu', 'active'];
    return model.deleteIn(key)
  }),
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
