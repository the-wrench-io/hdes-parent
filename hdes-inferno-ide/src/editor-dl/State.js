const ID = 'editordl'
const init = {
  active: undefined, //id
  entries: [],
  chord: undefined,
}

// all delete actions
const actions = app => update => ({
  init: () => app(({ actions }) => {
  }),
  load: () => app(({ actions }) => update(model => {

    return model.setIn([ID, 'active'], true);
  })),
  mark: (entry) => app(({ actions }) => update(model => {
    return model.updateIn([ID, 'entries'], e => {
      const contains = e.findIndex(e => e.src.id === entry.src.id);
      return contains === -1 ? e.push(entry) : e
    });
  })),
  unmark: (entry) => app(({ actions }) => update(model => {
    return model.updateIn([ID, 'entries'], e => {
      const contains = e.findIndex(e => e.src.id === entry.src.id);
      return contains === -1 ? e : e.delete(contains)
    });
  })),
  delete: (entries) => app(({ actions }) => { 

    actions.backend.delete(entries.map(e => e.src), 
    (data) => {
      // success
      update(model => { return model; })
    },
    (data) => {
      // errors
      update(model => { return model; })
    })
  }),
  setChord: (data) => app(({ actions }) => update(model => {
    return model.setIn([ID, 'chord'], data);
  }))
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}