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

export class EditorTabs extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['editor']
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))
  }
  render() {
    const { actions, state } = this.props
    const open = state.getIn(['editor', 'entry', 'id'])
    const entries = state.getIn(['editor', 'entries']).toJS().map(id => {

      const isOpen = open === id;
      const entryStyle = isOpen ? 'is-active' : null;
      const entry = state.getIn(['explorer', 'entries']).filter(e => e.get('id') === id).get(0);

      // Entry is not present anymore
      if(!entry) {
        return null;
      }

      const openEntry = () => actions.explorer.openEntry(id);
      const type = entry.get('type')
      const isSaving = state.getIn(['editor', 'saving', id]) ? true : false
      const savingErrorsKey = ['editor', 'saving', id, 'errors']
      const isSavingErrors = state.getIn(savingErrorsKey) ? state.getIn(savingErrorsKey).toJS().length > 0 : false
      
      return (<li class={entryStyle}>
        <a href={'#entry/' + id} onClick={openEntry} >
          <div class='columns is-1'>
            <div class='column'>
              {isSaving && !isSavingErrors ? <i class='is-saving las la-asterisk'></i> : null}
              {isSaving && isSavingErrors ? <i class='is-saving-error las la-asterisk'></i> : null}
              <span class='is-type icon is-smallhas-text-left'>{type}</span>
              <span>{entry.get('name')}</span>
            </div>
            <div class='column' onClick={() => actions.explorer.closeEntry(id)}>
              <i class='is-close icon is-small has-text-right las la-times'></i>
            </div>
          </div>
        </a></li>)
    })

    return (<div class='tabs is-boxed editor-tb'><ul>{entries}</ul></div>)
  }
}
