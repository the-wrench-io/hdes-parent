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
  saving: {
    // id : { delay: 5000, value: 'valueToSave' }
  },
  saveDelay: 5000
}

// all explorer actions
const actions = ({ update, actions }) => ({
  init: () => {},
 
  open: (entry) => {
    update(model => model.setIn([ID, 'entry'], entry));

    const type = entry.get('type');
    if(type === 'fl') {
      actions.editorfl.load();
    } else if(type === 'delete') {
      actions.editordl.load();
    }
  },

  delayedSave: (delayKey) => setTimeout(() => update(model => {
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

    actions.backend.service.save(
      { id: model.getIn(delayKey).get('id'), 
        value: model.getIn(delayKey).get('value') },
      data => {},
      errors => {})
    return model.deleteIn(delayKey)
  }), 1000),

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

  close: (entry) => update(model => model.deleteIn([ID, 'entry']))
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
