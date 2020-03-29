import Immutable from 'immutable'

const ID = 'iconbar'
const init = {
  explorer: { enabled: false },
  search: { enabled: false },
  debug: { enabled: false },
  changes: { enabled: false },
  create: { enabled: false },
  delete: { enabled: false },
};

const toggle = (model, name) => {
  const currentValue = model.getIn([ID, name, 'enabled']);
  return model
    .setIn([ID, 'explorer', 'enabled'], false)
    .setIn([ID, 'search', 'enabled'], false)
    .setIn([ID, 'debug', 'enabled'], false)
    .setIn([ID, 'changes', 'enabled'], false)
    .setIn([ID, 'create', 'enabled'], false)
    .setIn([ID, 'delete', 'enabled'], false)
    .setIn([ID, name, 'enabled'], !currentValue);
}

// all explorer actions
const actions = app => update => ({
  toggleExplorer: () => {
    update(model => toggle(model, 'explorer'))
  },
  toggleSearch: () => {
    update(model => toggle(model, 'search'))
  },
  toggleDebug: () => {
    update(model => toggle(model, 'debug'))
  },
  toggleChanges: () => {
    update(model => toggle(model, 'changes'))
  },
  toggleNewitem: () => {
    update(model => toggle(model, 'create'))
  },
  toggleDelete: () => app(({ actions }) => update(model => {
    const result = toggle(model, 'delete');
    const entry = Immutable.fromJS({type: 'delete', id: 'delete-view-entry'});

    if(result.getIn([ID, 'delete', 'enabled'])) {
      actions.editor.open(entry)
    } else {
      actions.editor.close(entry)
    }
    return result;


  }))
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}