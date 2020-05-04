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
import { DecisionTable } from './DecisionTable'

const ToolbarItem = ({icon, tooltip, onClick}) => {
  return (
    <div class='button is-fullwidth has-tooltip-right' data-tooltip={tooltip} onClick={onClick}>
      <span class={`las ${icon} is-size-4 icon`} />
    </div>)
}


export class DtView extends Component {
  constructor(props) {
    super(props);
    this.onCellEdit = this.onCellEdit.bind(this)
    this.onChangeStart = this.onChangeStart.bind(this)
    this.onChangeCancel = this.onChangeCancel.bind(this)
  }
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['editordt']
    return (
      !this.props.state.getIn(key).equals(nextProps.state.getIn(key)) ||
      this.props.entry.get('id') !== nextProps.entry.get('id'))
  }
  componentDidMount() {
    const { actions, entry } = this.props
    actions.editordt.load(entry)
  }
  componentDidUpdate(prevProps) {
    const { actions, entry } = this.props
    if(this.props.entry.get('id') !== prevProps.entry.get('id')) {
      actions.editordt.load(entry)
    }
  }
  onCellEdit(cellId) {
    const { actions, state, entry } = this.props
    const id = entry.get('id')
    actions.editordt.onCellEdit(id, cellId)
  }
  onChangeStart(editable) {
    const { actions, state, entry } = this.props
    const id = entry.get('id')
    actions.editordt.onChangeStart(id)
  }
  onChangeCancel(editable) {
    const { actions, state, entry } = this.props
    const id = entry.get('id')
    actions.editordt.onChangeCancel(id)
  }
  render() {
    const { actions, state, entry } = this.props
    const id = entry.get('id')
    const loaded = state.getIn(['editordt', 'models', id])

    if(!loaded) {
      return <div>Loading...</div>
    }
    const model = loaded.toJS()
    const editable = state.getIn(['editordt', 'editable', id]).toJS()

    return (<div class='tile dt-editor'>
      <div class='tile is-child is-toolbar'>
        <ToolbarItem icon='la-caret-square-right' tooltip='Add new column'/>
        <ToolbarItem icon='la-caret-square-down' tooltip='Add new row'/>
        <ToolbarItem icon='la-arrows-alt-h' tooltip='Move columns'/>
        <ToolbarItem icon='la-arrows-alt-v' tooltip='Move rows'/>
        <ToolbarItem icon='la-eraser' tooltip='Remove columns or rows'/>
      </div>
      <div class='tile is-child is-content'>
        <DecisionTable view={model.view} editable={editable} 
          onCellEdit={this.onCellEdit} 
          onChangeStart={this.onChangeStart}
          onChangeCancel={this.onChangeCancel} />
      </div>
    </div>)
  }
}
