const source = [
  {id: "1", rev: 0, label: 'dt', name: 'dt 1', in: []},
  {id: "2", rev: 0, label: 'dt', name: 'dt 2', in: []},
  {id: "3", rev: 0, label: 'dt', name: 'dt 3', in: []},
  {id: "4", rev: 0, label: 'dt', name: 'dt 4', in: []},
  {id: "5", rev: 0, label: 'dt', name: 'dt 5', in: []},
  {id: "6", rev: 0, label: 'dt', name: 'dt 6', in: []},
  {id: "7", rev: 0, label: 'dt', name: 'dt 7', in: []},
  {id: "8", rev: 0, label: 'dt', name: 'dt 8', in: []},
  {id: "9", rev: 0, label: 'dt', name: 'dt 9', in: []},
  {id: "10", rev: 0, label: 'dt', name: 'dt 10', in: []},
  {id: "11", rev: 0, label: 'dt', name: 'dt 11', in: []},
  {id: "12", rev: 0, label: 'dt', name: 'dt 12', in: []},
  {id: "13", rev: 0, label: 'dt', name: 'dt 13', in: []},
  {id: "14", rev: 0, label: 'dt', name: 'dt 14', in: []},

  {id: "15", rev: 0, label: 'st', name: 'st 1', in: []},
  {id: "16", rev: 0, label: 'st', name: 'st 2', in: []},
  {id: "17", rev: 0, label: 'st', name: 'st 3', in: []},
  {id: "18", rev: 0, label: 'st', name: 'st 4', in: []},
  {id: "19", rev: 0, label: 'st', name: 'st 5', in: []},
  {id: "20", rev: 0, label: 'st', name: 'st 6', in: []},
  {id: "21", rev: 0, label: 'st', name: 'st 7', in: []},
  {id: "22", rev: 0, label: 'st', name: 'st 8', in: []},
  {id: "23", rev: 0, label: 'st', name: 'st 9', in: []},
  {id: "24", rev: 0, label: 'st', name: 'st 10', in: []},
  {id: "25", rev: 0, label: 'st', name: 'st 11', in: []},

  {id: "26", rev: 0, label: 'fl', name: 'flow 1', in: ['1', '2', '3', '15', '16', '25']},
  {id: "27", rev: 0, label: 'fl', name: 'flow 2', in: ['1', '12', '13', '15', '16', '25']},
  {id: "28", rev: 0, label: 'fl', name: 'flow 3', in: ['1', '25', '15']},
  {id: "29", rev: 0, label: 'fl', name: 'flow 4', in: ['24', '3', '5', '6', '14']}, 
]


const calcDeps = (e) => e.deps.in.length + e.deps.out.length

const createInput = (src) => {
  const error = {
    src: {
      id: "error-connection", 
      label: 'error', 
      name: 'connection error'
    },
    deps: { in: [], out: [], count: 0 }
  }

  // Create wrappers
  const byId = {};
  for(let origin of src) {
    const entry = {
      src: origin,
      deps: { in: [], out: [], count: 0 }
    }
    byId[origin.id] = entry;
  }

  // associate connections
  for(let origin of src) {
    const entry = byId[origin.id]

    for(let id of origin.in) {
      const imported = byId[id];
      if(!imported) {
        entry.deps.in.push(error.src.id);
        error.deps.out.push(id)
      } else {
        entry.deps.in.push(id)
        imported.deps.out.push(entry.src.id);
      }
    }
  }


  if(calcDeps(error) !== 0) {
    byId[error.src.id] = error;
  }

  for(let value of Object.values(byId)) {
    value.deps.count = calcDeps(value);
  }
  
  return byId;
}

export const data = createInput(source);