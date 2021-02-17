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
const ID = 'editordl'
const init = {
  active: undefined, //id
  entries: [],
  chord: undefined,
}

// all delete actions
const actions = ({actions, update}) => ({
  init: () => {
  },
  load: () => update(model => {

    return model.setIn([ID, 'active'], true);
  }),
  mark: (entry) => update(model => {
    return model.updateIn([ID, 'entries'], e => {
      const contains = e.findIndex(e => e.src.id === entry.src.id);
      return contains === -1 ? e.push(entry) : e
    });
  }),
  unmark: (entry) => update(model => {
    return model.updateIn([ID, 'entries'], e => {
      const contains = e.findIndex(e => e.src.id === entry.src.id);
      return contains === -1 ? e : e.delete(contains)
    });
  }),
  delete: (entries) => { 

    actions.backend.delete(entries.map(e => e.src), 
    (data) => {
      // success
      update(model => { return model; })
    },
    (data) => {
      // errors
      update(model => { return model; })
    })
  },
  setChord: (data) => update(model => {
    return model.setIn([ID, 'chord'], data);
  })
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
