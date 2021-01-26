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
import { RowBoolean, RowEdit, RowDropdown } from './Row'


export class Input extends Component {
  render() {
    const { view, node, actions, state } = this.props
    const { required, type } = node
    const options = view.types.map(t => {return {value: t.value}})

    const result = []
    result.push(<RowEdit id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowBoolean actions={actions} state={state} id={required.start} keyword={required.keyword} indent={required.indent} value={required.value}/>)
    result.push(<RowDropdown actions={actions} state={state} id={type.start} keyword={type.keyword} indent={type.indent} value={type.value} options={options}/>)
    return result
  }
}
