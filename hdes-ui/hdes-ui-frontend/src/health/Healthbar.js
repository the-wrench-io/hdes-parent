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
import { Component } from 'inferno';


const getConnection = (state) => {
  const connection = state.getIn(['health', 'connection'])
  if(connection) {
    return <div></div>
  }
  return <div>
    <span class='icon'><i class='fas fa-skull-crossbones is-size-6' /></span>
    <span>lost connection</span>
  </div>
}

const getTag = (state) => {
  const value = state.getIn(['health', 'status'])

  let tag = value.storage.type
  if(value.storage.type === 'LOCAL') {
    tag += '@' + value.storage.location
  }

  return <div>
    <span class='icon'><i class='las la-database is-size-6 icon-small' /></span>
    <span>{tag}</span>
  </div>
}

const getColor = (state) => {
  const connection = state.getIn(['health', 'connection'])
  return connection ? 'has-background-info' : 'has-background-danger'
}

export class Healthbar extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['health'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { state } = this.props;
    return (<div class={getColor(state)}>
        <nav class="level is-ide-info-contents">
          <div class="level-left">
            <div class="level-item">{getTag(state)}</div>
            <div class="level-item">{getConnection(state)}</div>
          </div>
          <div class="level-right">
            <div class="level-item">
              <strong>All</strong>
            </div>
          </div>
        </nav>
      </div>
    );
  }
}
