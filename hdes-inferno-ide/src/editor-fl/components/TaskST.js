import { Component } from 'inferno'
import { Row, RowDropdown } from './Row'
import { TaskInputs } from './TaskInputs'


export class TaskST extends Component {
  render() {
    const { view, node, state, actions } = this.props
    const inputs = node.inputsNode
    const ref = node.ref
    const result = []
    const options = state.getIn(['explorer', 'entries']).toJS()
    .filter(e => e.type === 'st').map(e => { return {
      id: e.id,
      value: e.name
    }})

    result.push(<Row id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowDropdown options={options} id={ref.start} keyword={ref.keyword} indent={ref.indent} value={ref.value} actions={actions} state={state}/>)
    result.push(<TaskInputs id={inputs.start} node={inputs} view={view} />)
    return result
  }
}