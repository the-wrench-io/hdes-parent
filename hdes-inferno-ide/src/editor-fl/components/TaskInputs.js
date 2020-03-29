import { Component } from 'inferno'
import { Row } from './Row'


export class TaskInputs extends Component {
  render() {
    const { view, node } = this.props
    const result = []
    result.push(<Row id={node.start} keyword={node.keyword} indent={node.indent} view={view} />)
    
    //for(let entry of Object.values(node.children).sort((e1, e2) => e1.start - e2.start)) {
    //  result.push(<Row id={entry.start} keyword={entry.keyword} indent={entry.indent} value={entry.value}/>)
    //}
    return result
  }
}