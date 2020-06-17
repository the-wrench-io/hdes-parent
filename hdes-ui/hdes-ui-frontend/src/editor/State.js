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

const ID = 'editor'
const init = {
  entry: undefined,
  entries: [], // entry id-s
  saving: {
    // id : { delay: 5000, value: 'valueToSave', errors: [] }
  },
  annotations: {
    // id : []
  },
  saveDelay: 2000,
  saveDelayFailRetry: 5000
}

// all explorer actions
const actions = ({ update, actions }) => ({
  init: () => {},
 
  setAnnotations: (id, tokens) => update(model => 
    model.setIn([ID, 'annotations', id], Immutable.fromJS(tokens))
  ),

  open: (entry) => update(model => model
    .updateIn([ID, 'entries'], e => {
      const id = entry.get('id')
      const contains = e.indexOf(id)
      return contains > -1 ? e : e.push(id);
    })
    .setIn([ID, 'entry'], entry)
  ),

  delayedSave: (delayKey, delay) => setTimeout(() => update(model => {
    const saving = model.getIn(delayKey);
    if(!saving) {
      console.log('saving key removed')
      return
    }

    const timeTillSave = saving.get('delay')
    if(timeTillSave > 0) {
      actions.editor.delayedSave(delayKey)
      return model.updateIn(delayKey, v => v.set('delay', timeTillSave - 1000))
    }

    const id = model.getIn(delayKey).get('id')
    const entityToSave = { id: id, value: model.getIn(delayKey).get('value') }

    actions.backend.service.save(entityToSave,
      data => {
        actions.backend.update(data)
        update(model => { 
          const updatedEntry = data.filter(e => e.id === id)[0];

          // update editable entry
          return model
            .setIn([ID, 'entry'], Immutable.fromJS(updatedEntry))
            .deleteIn(delayKey) 
        })
      },
      errors => {
        console.error(errors)
        // push errors
        update(model => model.setIn([...delayKey, 'errors'], Immutable.fromJS(errors)))

        // try again later
        actions.editor.delayedSave(delayKey, init.saveDelayFailRetry)
      })

  }), delay ? delay : init.saveDelay),

  save: ({entry, value}) => {
    const id = entry.get('id')
    const delayKey = [ID, 'saving', id];
    
    update(model => {
      if(model.getIn([ID, 'entry', 'value']) === value) {
        return
      }
      const saving = model.getIn(delayKey);  
      
      // Schedule saved delay if its not present
      if(!saving) {
        actions.editor.delayedSave(delayKey, entry, value)
      }

      return model.setIn(delayKey, Immutable.fromJS({delay: init.saveDelay, id: id, value: value}))
    })
  },

  close: (entry) => update(model => model
    .updateIn([ID, 'entries'], e => {
      const id = entry.get('id')
      const contains = e.indexOf(id)
      return contains > -1 ? e.delete(id) : e;
    })
    .updateIn([ID], e => e.getIn(['entry', 'id']) === entry.get('id') ? e.delete('entry') : e)
    )
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
