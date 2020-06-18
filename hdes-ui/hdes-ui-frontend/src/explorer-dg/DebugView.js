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
import { default as CodeMirror } from 'codemirror'
import 'codemirror/addon/display/autorefresh'

import 'codemirror/mode/clike/clike'


const getDef = (state) => {
  const explorer = state.getIn(['explorer'])
  const entryOpen = explorer.get('entryOpen')
  if(!entryOpen) {
    return null
  }
  
  return explorer.get('entries').filter(e => e.get('id') === entryOpen).toJS()[0]
}

const getSummary = (state, def) => {
  if(!def) {
    return null
  }
  return state.getIn(['debug', 'outputs', def.id])
}

const getDebugField = (def, input, actions) => {
  return <div class='field is-small'>
    <div class='control is-small'>
      <label class='label debug-label'>{input.name}</label>
      <input onInput={e => actions.debug.setInputFieldValue(def, input, e.target.value)}
        onClick={e => actions.debug.setInputFieldActive(def, input)}
        class='input is-primary is-small' 
        type='text' placeholder={input.name} 
        defaultValue={input.debugValue.empty ? '' : input.debugValue} />
    </div>
  </div>
}

const getDebugInput = (def, summary, actions) => {
  if(!def.ast.inputs.length) {
    return <div class='notification is-primary'>Resource has no inputs!</div>
  }
  const inputs = def.ast.inputs.map(input => getDebugField(def, input, actions))

  let src;
  if(summary && summary.resources.length > 0) {
    src = <button class='button is-fullwidth' onClick={() => actions.debug.modal(true)}>open java sources</button>
  } else {
    src = <button class='button is-fullwidth' disabled>open java sources</button>
  }

  const runButton = <div class='field control'>
    <button class='button is-fullwidth' onClick={() => actions.debug.run(def)}>run and show summary</button>
    {src}
  </div>

  return [runButton, ...inputs]
}

const getModal = (def, textareaRef, summary, state, actions) => {
  const active = state.getIn(['debug', 'modal', 'enabled']) ? ' is-active' : ''

  return (<div class={ 'modal' + active}>
    <div class='modal-background'></div>
    <div class='modal-card src-editor'>
      <header class='modal-card-head'>
        <p class='modal-card-title'>Generated Java Sources</p>
        <button class='delete' aria-label='close' onClick={() => actions.debug.modal(false)}></button>
      </header>
      <section class='modal-card-body'>
        <textarea ref={textareaRef} defaultValue={getJavaSource(summary)}/>
      </section>
    </div>
  </div>)
}

const getJavaSource = (summary) => {
  if(!summary) {
    return ''
  }
  let result = ''
  for(let r of summary.resources) {
    for(let d of r.declarations) {
      result = result + '// START OF \' ' + d.type.name + ' \'\r\n'
      result = result + d.value
      result = result + '\r\n\r\n\r\n'
    }
  }
  return result
}


export class DebugView extends Component {

  constructor(props) {
    super(props)
    this.textareaRef = createRef()
  }

  shouldComponentUpdate(nextProps, nextState) {
    const key0 = ['debug']
    const key1 = ['explorer']
    return !this.props.state.getIn(key0).equals(nextProps.state.getIn(key0)) || 
      !this.props.state.getIn(key1).equals(nextProps.state.getIn(key1));
  }

  componentDidMount() {
    this.editor = CodeMirror.fromTextArea(this.textareaRef.current, {
      mode: 'text/x-java',
      autoRefresh: true,
      lineNumbers: true,
      tabSize: 2,
      firstLineNumber: 1,
      theme: 'neo',
    })
  }

  componentDidUpdate(prevProps) {
    const getSrc = (state) => {
      const def = getDef(state)
      const summary = getSummary(state, def)
      return getJavaSource(summary)
    }
    const src = getSrc(this.props.state)
    
    if(src !== getSrc(prevProps.state)) {
      this.editor.getDoc().setValue(src)
      this.editor.refresh()
    }
  }

  render() {
    const { actions, state } = this.props
    const def = getDef(state)
    const summary = getSummary(state, def)
    const debug = def ? getDebugInput(def, summary, actions) : <div class='notification is-primary'>No resources open to debug!</div>
    const modal = getModal(def, this.textareaRef, summary, state, actions)

    return (
      <aside class='debug'>
        <ul class='menu-list'>
          <li class='explorer-title'>debug</li>
          {debug}
          {modal}
        </ul>
      </aside>
    );
  }
}
