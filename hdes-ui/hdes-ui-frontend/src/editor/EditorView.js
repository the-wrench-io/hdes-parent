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
import { EditorTx } from './../editor-tx'
import { DlView } from './../editor-dl'
//import { DtView } from './../editor-dt'
//import { FlView } from './../editor-fl'


const createComponent = (actions, state) => {
  const entry = state.getIn(['editor', 'entry']);
  
  if(!entry) {
    if(state.getIn(['iconbar', 'delete', 'enabled'])) {
      return <DlView actions={actions} state={state} />
    }

    return null;
  }

  const type = entry.get('type')
  const result = []

  const id = entry.get('id')
  const savingErrorsKey = ['editor', 'saving', id, 'errors']
  const savingErrors = state.getIn(savingErrorsKey) ? state.getIn(savingErrorsKey).toJS() : []

  if(savingErrors.length > 0) {
    const messages = savingErrors.map(e => [<div>{e.defaultMessage}</div>,<div><strong>Log: </strong>{JSON.stringify(e.values)}</div>])
    result.push(<div class='notification is-danger is-error-messages'>
      <button class='delete'></button>
      {messages}
    </div>)
  }

  if(type === 'delete') {
    result.push(<DlView actions={actions} state={state} />)
  } else {
    result.push(<EditorTx actions={actions} state={state} entry={entry} />)
  }

  return result
}

export class EditorView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    //const key = ['editor']
    //return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))

    return true;
  }

  render() {
    const { actions, state } = this.props
    return createComponent(actions, state)
  }
}
