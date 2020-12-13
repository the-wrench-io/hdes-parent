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
import { Component } from 'inferno'
import { RowDropdown, Row } from './Row'
import { TaskInputs } from './TaskInputs'


export class TaskDT extends Component {
  render() {
    const { view, node, actions, state } = this.props
    const inputs = node.inputsNode
    const ref = node.ref
    const result = []

    const options = state.getIn(['explorer', 'entries']).toJS()
    .filter(e => e.type === 'dt').map(e => { return {
      id: e.id,
      value: e.name
    }})
    result.push(<Row id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowDropdown options={options} id={ref.start} keyword={ref.keyword} indent={ref.indent} value={ref.value} actions={actions} state={state}/>)
    result.push(<TaskInputs id={inputs.start} node={inputs} view={view} actions={actions} state={state} />)
    return result
  }
}
