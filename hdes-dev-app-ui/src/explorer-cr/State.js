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

const ID = 'create'
const init = {
  value: '',
  errors: [
    {id: '1', value: 'name can not be empty'}
  ]
}

const actions = app => update => ({
  init: () => app(({ actions }) => {
  }),
  setTypeName: (value) => {
    update(model => model.setIn([ID, 'value'], value))
  },
  create: (type, name) => app(({ actions }) => {
    const onSuccess = (entries) => actions.backend.model(() => {
      entries.forEach(e => actions.explorer.openEntry(e.id));
      actions.iconbar.toggleExplorer();
    })

    const onError = (errors) => {
      const result = errors.map(e => { return {id: e.id, value: e.defaultMessage}});
      update(model => model.setIn([ID, 'errors'], Immutable.fromJS(result)))
    };
    actions.backend.service.create({name: name, type: type}, onSuccess, onError)
  }),
  deleteError: (id) => update(model => {
    const index = model.getIn([ID, 'errors']).findIndex(e => e.get('id') === id)
    return model.deleteIn([ID, 'errors', index])
  })
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}
