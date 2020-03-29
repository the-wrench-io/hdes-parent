import { Component } from 'inferno'
import { RowBoolean, RowEdit, RowDropdown } from './Row'


export class Input extends Component {
  render() {
    const { view, node, actions, state } = this.props
    const { required, type } = node
    const options = view.types.map(t => {return {value: t.value}})

    const result = []
    result.push(<RowEdit id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowBoolean actions={actions} state={state} id={required.start} keyword={required.keyword} indent={required.indent} value={required.value}/>)
    result.push(<RowDropdown actions={actions} state={state} id={type.start} keyword={type.keyword} indent={type.indent} value={type.value} options={options}/>)
    return result
  }
}