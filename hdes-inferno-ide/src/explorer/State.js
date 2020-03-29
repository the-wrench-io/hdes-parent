import Immutable from 'immutable'

const ID = 'explorer'
const init = {
  entriesOpen: [], // dt,st,fl
  entriesEditing: [],
  entryOpen: undefined, //id of the opened entry
  entries: [
    // {id: '1', type: 'dt', name: "uw-dt1"},
  ]
}

// all explorer actions
const actions = app => update => ({
  setEntries: (entries) => app(({ actions }) => {
    update(model => model.setIn([ID, 'entries'], Immutable.fromJS(entries)))
    
    actions.explorer.openEntry('cascoAdditionalFactors');

  }),
  openEntry: (entryId) => app(({ actions }) => update(model => {
    model.getIn([ID, 'entries'])
      .filter(e => e.get('id') === entryId)
      .forEach(e => actions.editor.open(e));

    return model
      .setIn([ID, 'entryOpen'], entryId)
      .updateIn([ID, 'entriesEditing'], e => e.indexOf(entryId) > -1 ? e : e.push(entryId))
    }
  )),
  closeEntry: (entryId) => app(({ actions }) => update(model => {
    model.getIn([ID, 'entries'])
    .filter(e => e.get('id') === entryId)
    .forEach(e => actions.editor.close(e));

    return model
      .updateIn([ID, 'entriesEditing'], e => {
        const contains = e.indexOf(entryId);
        return contains > -1 ? e.delete(contains) : e;
      })
      .updateIn([ID], (explorer) => {
        if(explorer.get('entryOpen') === entryId) {
          const lastEntry = explorer.getIn(['entriesEditing', -1]);
          return explorer.set('entryOpen', lastEntry);
        }
        return explorer;
      })
  })),
  toggleEntries: (type) => {
    update(model => model.updateIn([ID, 'entriesOpen'], e => {
      const contains = e.indexOf(type);
      return contains > -1 ? e.delete(contains) : e.push(type);
    }))
  }
})

export const State = app => {
  return {
    id: ID,
    initial: init,
    actions: actions(app)
  }
}