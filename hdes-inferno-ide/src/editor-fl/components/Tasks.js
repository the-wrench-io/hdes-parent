import { Component } from 'inferno'
import { Row } from './Row'
import { Task } from './Task'

export class Tasks extends Component {
  render() {
    const { view, actions, state } = this.props
    const result = []

    if(!view.children.tasks) {
      return result;
    }
    
    const tasks = view.children.tasks;
    result.push(<Row id={tasks.start} keyword={tasks.keyword} />)

    for(let task of Object.values(tasks.children).sort((e1, e2) => e1.start - e2.start)) {
      result.push(<Task view={view} node={task} actions={actions} state={state} />)
    }

    return result
  }
}