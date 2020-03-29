import { Component } from 'inferno'
import { RowEdit, RowDropdown } from './Row'
import { TaskDT } from './TaskDT'
import { TaskST } from './TaskST'
import { TaskSW } from './TaskSW'


export class Task extends Component {
  render() {
    const { view, node, actions, state } = this.props
    const { id, then } = node
    const result = []

    result.push(<RowEdit id={node.start} keyword={node.keyword} indent={node.indent} />)
    result.push(<RowEdit id={id.start} keyword={id.keyword} indent={id.indent} value={id.value} />)

    if(then) {
      const options = Object.values(view.tasks).map(t => { return {
        value: t.id.value
      }})
      options.push({id: 'end', value: 'end'})
      result.push(<RowDropdown id={then.start} keyword={then.keyword} indent={then.indent} value={then.value} actions={actions} state={state} options={options}/>)
    }

    if(node.decisionTable) {
      result.push(<TaskDT id={node.start} view={view} node={node.decisionTable} actions={actions} state={state}/>)
    } else if(node.service) {
      result.push(<TaskST id={node.start} view={view} node={node.service} actions={actions} state={state}/>)
    } else if(node.switch) {
      result.push(<TaskSW id={node.children.switch.start} view={view} node={node.children.switch} actions={actions} state={state}/>)
    }

    //for(let entry of Object.values(node.children).sort((e1, e2) => e1.start - e2.start)) {
    //  result.push(<Row id={entry.start} keyword={entry.keyword} indent={entry.indent} value={entry.value}/>)
    //}
    return result
  }
}