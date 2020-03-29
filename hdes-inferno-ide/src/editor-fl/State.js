const ID = 'editorfl'
const init = {
  models: {},
  active: undefined, //id
  menu: {
    active: undefined // no
  }
}

// all explorer actions
const actions = app => update => ({
  init: () => app(({ actions }) => {
  }),
  load: () => app(({ actions }) => update(model => {
    const entry = model.getIn(['editor', 'entry'])
    const id = entry.get('id');
    
    return model
      .updateIn([ID, 'models'], models => models.get(id) ? models : models.set(id, entry))
      .setIn([ID, 'active'], id);
  })),
  toggleActive: (id) => app(({ actions }) => update(model => {
    const key = [ID, 'menu', 'active'];
    return model.getIn(key) === id ? model.deleteIn(key) : model.setIn(key, id)
  })),
  unsetActive: (id) => app(({ actions }) => update(model => {
    const key = [ID, 'menu', 'active'];
    return model.deleteIn(key)
  })),
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}