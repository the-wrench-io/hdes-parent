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
import { Row } from './Row'


export class TaskSW extends Component {
  render() {
    const { view, node } = this.props
    const result = []

    result.push(<Row id={node.start} keyword={node.keyword} indent={node.indent} view={view}/>)
    

    //for(let entry of Object.values(node.children).sort((e1, e2) => e1.start - e2.start)) {
    //  result.push(<Row id={entry.start} keyword={entry.keyword} indent={entry.indent} value={entry.value}/>)
    //}
    return result
  }
}
