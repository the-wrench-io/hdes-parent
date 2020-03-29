let connectionUp;
const ID = 'health'
const init = {
  init: { enabled: true, loading: true, log: [] },
  connection: undefined
}

const addlog = (update, value) => update(model => {
  return model.updateIn([ID, 'init', 'log'], 
    log => log.push(value))
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
const actions = app => update => ({
  init: () => app(({ actions }) => {
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
  })
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}