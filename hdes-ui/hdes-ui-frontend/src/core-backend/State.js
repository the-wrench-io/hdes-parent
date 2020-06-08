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

const ID = 'backend'
const init = {

}

// all explorer actions
const actions = (store, service) => ({
  
  model: (callback) => {
    service.models(
      (data) => {
        store.actions.explorer.setEntries(data)
        if(callback) {
          callback()
        }
      }
    );
  },

  update: (data) => store.actions.explorer.setEntries(data),

  delete: (entries, successCallback, errorCallback) => {
    const success = (data) => {console.log('delete-success', data)}
    const error = (data) => {console.log('delete-error', data)}
    service.delete(entries, success, error);
  },

  service: service

})

export const State = (store, backend) => {
  return {
    id: ID,
    initial: init,
    actions: actions(store, backend)
  }
}
