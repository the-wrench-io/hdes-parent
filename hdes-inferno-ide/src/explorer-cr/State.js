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