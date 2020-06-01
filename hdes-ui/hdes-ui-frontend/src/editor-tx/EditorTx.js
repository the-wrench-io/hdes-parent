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
import { Component, createRef } from 'inferno'
import {default as CodeMirror} from 'codemirror'

export class EditorTx extends Component {

  constructor(props) {
    super(props)
    this.textareaRef = createRef()
  }
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['editortx']
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key)) ||
      this.props.entry.get('id') !== nextProps.entry.get('id')
  }
  componentDidMount() {
    const { actions, entry } = this.props
    actions.editortx.load(entry)
  }
  componentDidUpdate(prevProps) {
    const { actions, entry } = this.props
    if(!this.editor) {
      this.editor = CodeMirror.fromTextArea(this.textareaRef.current, {
        mode: 'hdes',
        lineNumbers: true,
        tabSize: 2,
        firstLineNumber: 1,
        gutters: ['CodeMirror-lint-markers'],
        theme: 'abcdef'
      })
    }
    if(this.props.entry.get('id') !== prevProps.entry.get('id')) {
      actions.editortx.load(entry, this.editor)
    }
  }
  render() {
    const { actions, state, entry } = this.props
    const active = state.getIn(['editortx', 'active'])

    if(!active) {
      return <div>Loading...</div>
    }

    const model = state.getIn(['editortx', 'models', active]).toJS()
    return (<div class='tile editor-tx'>
      <textarea id={active} ref={this.textareaRef} defaultValue={model.value}/>
    </div>
    )
  }
}
