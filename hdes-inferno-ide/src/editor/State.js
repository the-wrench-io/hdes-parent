const ID = 'editor'
const init = {
  entry: undefined
}



// all explorer actions
const actions = app => update => ({
  init: () => app(({ actions }) => {
  }),
  open: (entry) => app(({ actions }) => {
    update(model => model.setIn([ID, 'entry'], entry));
    const type = entry.get('type');

    if(type === 'fl') {
      actions.editorfl.load();
    } else if(type === 'delete') {
      actions.editordl.load();
    }

    
  }),
  close: (entry) => update(model => {
    return model.deleteIn([ID, 'entry']);
  })
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}