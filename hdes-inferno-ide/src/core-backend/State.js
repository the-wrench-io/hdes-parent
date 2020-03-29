
const ID = 'backend'
const init = {

}

// all explorer actions
const actions = (app, service) => update => ({
  model: (callback) => app(state => {
    service.models(
      (data) => {
        state.actions.explorer.setEntries(data)

        if(callback) {
          callback()
        }
      }
    );
  }),
  delete: (entries, successCallback, errorCallback) => app(state => {
    const success = (data) => {console.log('delete-success', data)}
    const error = (data) => {console.log('delete-error', data)}
    service.delete(entries, success, error);
  }),
  service: service
})

export const State = (app, backend) => {
  return {
    id: ID,
    initial: init,
    actions: actions(app, backend)
  }
}