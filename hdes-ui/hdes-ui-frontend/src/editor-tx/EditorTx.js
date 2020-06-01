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

import 'codemirror/addon/mode/simple'

CodeMirror.defineSimpleMode("hdes", {
  // The start state contains the rules that are intially used
  start: [
    // The regex matches the token, the token property contains the type
    {regex: /'(?:[^\\]|\\.)*?(?:'|$)/, token: "string"},
    // You can match multiple tokens at once. Note that the captured
    // groups must span the whole string in this case
    {regex: /(function)(\s+)([a-z$][\w$]*)/,
     token: ["keyword", null, "variable-2"]},
    // Rules are matched in the order in which they appear, so there is
    // no ambiguity between this one and the one above
    {regex: /(?:form of groups|groups|from|FORMULA|fields|actions|dropdowns|headers|tasks|define|flow|decision-table|description|then|end as|ALL|MATRIX|FROM)\b/,
     token: "keyword"},
    {regex: /true|false/, token: "atom"},
    {regex: /0x[a-f\d]+|[-+]?(?:\.\d+|\d+\.?\d*)(?:e[-+]?\d+)?/i,
     token: "number"},
    {regex: /\/\/.*/, token: "comment"},
    {regex: /\/(?:[^\\]|\\.)*?\//, token: "variable-3"},
    // A next property will cause the mode to move to a different state
    {regex: /\/\*/, token: "comment", next: "comment"},
    {regex: /[-+\/*=<>!]+/, token: "operator"},
    // indent and dedent properties guide autoindentation
    {regex: /[\{\[\(]/, indent: true},
    {regex: /[\}\]\)]/, dedent: true},
    {regex: /[a-z$][\w$]*/, token: "variable"},
    // You can embed other modes with the mode property. This rule
    // causes all code between << and >> to be highlighted with the XML
    // mode.
    {regex: /<</, token: "meta", mode: {spec: "xml", end: />>/}}
  ],
  // The multi-line comment state.
  comment: [
    {regex: /.*?\*\//, token: "comment", next: "start"},
    {regex: /.*/, token: "comment"}
  ],
  // The meta property contains global information about the mode. It
  // can contain properties like lineComment, which are supported by
  // all modes, and also directives like dontIndentStates, which are
  // specific to simple modes.
  meta: {
    dontIndentStates: ["comment"],
    lineComment: "//"
  }
});

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
