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

const ID = 'editordt'
const init = {
  models: {},
  editable: {}
}

// all explorer actions
const actions = app => update => ({
  init: () => app(({ actions }) => {
  }),
  load: (entry) => app(({ actions }) => update(model => {
    const id = entry.get('id')
    return model
      .updateIn([ID, 'editable'], models => models.get(id) ? models : models.set(id, Immutable.fromJS({})))
      .updateIn([ID, 'models'], models => models.get(id) ? models : models.set(id, entry));
  })),
  onCellEdit: (modelId, cellId) => app(({ actions }) => update(model => {
    return model.setIn([ID, 'editable', modelId, 'id'], cellId)
  })),
  onChangeStart: (modelId) => app(({ actions }) => update(model => {
    return model.setIn([ID, 'editable', modelId, 'enabled'], true)
  })),
  onChangeCancel: (modelId) => app(({ actions }) => update(model => {
    return model.setIn([ID, 'editable', modelId, 'enabled'], false)
  }))
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}
