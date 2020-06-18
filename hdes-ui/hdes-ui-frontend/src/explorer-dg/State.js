/*-
 * #%L
 * hdes-dev-app-ui
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import Immutable from 'immutable'

const ID = 'debug'
const init = {
  inputs: {

    // id: { field : value }
  },
  outputs: {
    // id: {} // value
  },
  modal: {
    enabled: false
  }
}


const actions = ({update, actions}) => ({

  setInputFieldActive: (def, input) => {
    actions.editor.setAnnotations(def.id, [input.token])
  },
  setInputFieldValue: (def, input, value) => update(model => {
    const key = [ID, 'inputs', def.id]
    const result = model.getIn(key) ? model : model.setIn(key, Immutable.fromJS({}))
    return result.updateIn(key, v => v.set(input.name, value));
  }),
  run: (def) => update(model => {
    const key = [ID, 'inputs', def.id]
    const input = model.getIn(key) ? model.getIn(key).toJS() : {}
    
    // add debug values
    const inputKeys = Object.keys(input)
    for(let inputDef of def.ast.inputs) {
      if(inputKeys.includes(inputDef.name) || inputDef.debugValue.empty) {
        continue;
      }
      input[inputDef.name] = input.debugValue
    }

    actions.backend.debug({name: def.name, input}, 
      data => {
        update(model => model.setIn([ID, 'outputs', def.id], data))
        if(data.errors.length > 0) {
          actions.editor.setErrors(def.id, data.errors)
        } else {
          /*
          output:
            meta: time: 1
            values:
              0: 
                id: 0
                index: 0
                  token:
                  value: "?, ?, between 1 and 30, 20"
                  start: {line: 8, column: 4}
                  end: {line: 8, column: 28}
          */
          const annotations = []
          for(let entry of Object.values(data.output.meta.values)) {
            const token = entry.token
            const {start, end} = token
            annotations.push({
              startLine: start.line, startCol: start.column,
              endLine: end.line, endCol: end.column })
          }

          actions.editor.setAnnotations(def.id, annotations)
          actions.editor.setErrors(def.id, [])
          actions.supportbar.debug(true)
        }
      },
      error => console.error(error))
  }),
  modal: (enabled) => update(model => model.setIn([ID, 'modal', 'enabled'], enabled)),
  closeSummary: (def) => {
    actions.supportbar.debug(false)
  }
})

export const State = store => {
  return {
    id: ID,
    initial: init,
    actions: actions(store)
  }
}
