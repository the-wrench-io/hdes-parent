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
let connectionUp;
const ID = 'health'
const init = {
  init: { enabled: true, loading: true, log: [] },
  status: undefined,
  connection: undefined
}

const addlog = (update, value) => update(model => {
  return model.updateIn([ID, 'init', 'log'], 
    log => log.push(value))
})

const setStatus = (update, value) => update(model => {
  return model.setIn([ID, 'status'], value)
})

const loaded = (update) => update(model => {
  return model
    .setIn([ID, 'init', 'loading'], false)
    .setIn([ID, 'connection'], true)
})

const complete = (update) => update(model => {
  return model.setIn([ID, 'init', 'enabled'], false)
})

const errorHandler = (update, errors) => {
  console.error(errors)
  
  for(let error of errors) {

    if(error.id === 'errors.connection') {
      addlog(update, `Failed to connect to: '${error.values.url}' because: '${error.defaultMessage}'!`)
      break;
    }
    addlog(update, `Server failure: '${error.id}' because: '${error.defaultMessage}', log code: '${error.logCode}' !`)
  }
  loaded(update)
}

const isConnection = (errors) => {
  return errors.filter(e => e.id === 'errors.connection').length === 0;
}

const successHandler = (update, actions, success) => {
  addlog(update, `Server status: '${success.status}'!`)
  for(let entry of success.values) {
    addlog(update, `Server message id: '${entry.id}', '${entry.value}'.`)
  }
  addlog(update, 'Loading models!')
  setStatus(update, success)

  actions.backend.service.models(data => {
    actions.explorer.setEntries(data)
    addlog(update, `Found '${data.length}' model(s).`)
    addlog(update, `Loading IDE in 1s...`)
    loaded(update)
    setTimeout(() => complete(update), 1000);
  }, errors => errorHandler(update, errors))
}

const queryHealth = (update, actions) => {
  setTimeout(() => {
    try {
      actions.backend.service.health(
        data => {
          if(!connectionUp) {
            update(model => {
              model.setIn([ID, 'connection'], true)
              connectionUp = true;
            })
          }
        },
        errors => {
          if(!isConnection(errors)) {
            update(model => model.setIn([ID, 'connection'], false))
          }
        })
    } catch(e) {
      console.error(e)
    } finally {
      queryHealth(update, actions)  
    }
  }, 5000)
}

// all explorer actions
const actions = store => ({

  init: () => {
    const { update, actions } = store
    console.log(store);

    addlog(update, 'Getting connection...')
    actions.backend.service.health(
      data => setTimeout(() => {
        successHandler(update, actions, data);
        queryHealth(update, actions);
      }, 1000),
      errors => {
        errorHandler(update, errors)
        if(!isConnection(errors)) {
          addlog(update, `Trying to connect again...`)
          setTimeout(() => actions.health.init(), 5000)
        }
      })
  }


})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
