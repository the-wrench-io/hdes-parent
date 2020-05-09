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
import { Task } from './Task'

export class Tasks extends Component {
  render() {
    const { view, actions, state } = this.props
    const result = []

    if(!view.children.tasks) {
      return result;
    }
    
    const tasks = view.children.tasks;
    result.push(<Row id={tasks.start} keyword={tasks.keyword} />)

    for(let task of Object.values(tasks.children).sort((e1, e2) => e1.start - e2.start)) {
      result.push(<Task view={view} node={task} actions={actions} state={state} />)
    }

    return result
  }
}
