import { Component } from 'inferno'
import { RowDropdown, Row } from './Row'
import { TaskInputs } from './TaskInputs'


export class TaskDT extends Component {
  render() {
    const { view, node, actions, state } = this.props
    const inputs = node.inputsNode
    const ref = node.ref
    const result = []

    const options = state.getIn(['explorer', 'entries']).toJS()
    .filter(e => e.type === 'dt').map(e => { return {
      id: e.id,
      value: e.name
    }})
    result.push(<Row id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowDropdown options={options} id={ref.start} keyword={ref.keyword} indent={ref.indent} value={ref.value} actions={actions} state={state}/>)
    result.push(<TaskInputs id={inputs.start} node={inputs} view={view} actions={actions} state={state} />)
    return result
  }
}