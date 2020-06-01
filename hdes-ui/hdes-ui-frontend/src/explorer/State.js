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

const ID = 'explorer'
const init = {
  entriesOpen: [], // dt,st,fl
  entriesEditing: [],
  entryOpen: undefined, //id of the opened entry
  entries: [
    // {id: '1', type: 'dt', name: "uw-dt1"},
  ]
}

// all explorer actions
const actions = app => update => ({
  setEntries: (entries) => app(({ actions }) => {
    update(model => model.setIn([ID, 'entries'], Immutable.fromJS(entries)))
    console.log(entries)

  }),
  openEntry: (entryId) => app(({ actions }) => update(model => {
    model.getIn([ID, 'entries'])
      .filter(e => e.get('id') === entryId)
      .forEach(e => actions.editor.open(e));

    return model
      .setIn([ID, 'entryOpen'], entryId)
      .updateIn([ID, 'entriesEditing'], e => e.indexOf(entryId) > -1 ? e : e.push(entryId))
    }
  )),
  closeEntry: (entryId) => app(({ actions }) => update(model => {
    model.getIn([ID, 'entries'])
    .filter(e => e.get('id') === entryId)
    .forEach(e => actions.editor.close(e));

    return model
      .updateIn([ID, 'entriesEditing'], e => {
        const contains = e.indexOf(entryId);
        return contains > -1 ? e.delete(contains) : e;
      })
      .updateIn([ID], (explorer) => {
        if(explorer.get('entryOpen') === entryId) {
          const lastEntry = explorer.getIn(['entriesEditing', -1]);
          return explorer.set('entryOpen', lastEntry);
        }
        return explorer;
      })
  })),
  toggleEntries: (type) => {
    update(model => model.updateIn([ID, 'entriesOpen'], e => {
      const contains = e.indexOf(type);
      return contains > -1 ? e.delete(contains) : e.push(type);
    }))
  }
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}
