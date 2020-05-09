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


export class Section extends Component {
  render() {
    const { state, actions, type, name } = this.props;
    const isOpen = state.getIn(['explorer', 'entriesOpen']).indexOf(type) > -1;
    let isAtLeastOneEntryOpen = false;
    const sectionEntries = !isOpen ? [] : state.getIn(['explorer', 'entries'])
    .filter(e => e.get('type') === type).map(e => {
      const id = e.get('id');
      
      const isOpen = state.getIn(['explorer', 'entryOpen']) === id;
      const iconStyle = isOpen ? 'is-active' : null;
  
      if(isOpen) {
        isAtLeastOneEntryOpen = true
      }
  
      return <li class="is-entry">
        <a href={'#' + type + '/' + id} onClick={() => actions.explorer.openEntry(id)} class={iconStyle}>{e.get('name') ? e.get('name') : '<no name>'}</a>
      </li>
    });
    const iconStyle = isOpen ? 'la-angle-down' : 'la-angle-right';
    const linkStyle = isOpen && isAtLeastOneEntryOpen ? 'is-active' : null;
  
    return ([
      <li><a href={'#' + type} class={linkStyle} onClick={() => actions.explorer.toggleEntries(type)}><i class={'las ' + iconStyle + ' icon is-small'}/>{name}</a></li>,
      ...sectionEntries
    ])
  }
}
