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