import { Component } from 'inferno'
import { Row } from './Row'
import { Input } from './Input'


export class Inputs extends Component {
  render() {
    const { view, actions, state } = this.props
    const result = []
    if(!view.children.inputs) {
      return result;
    }
    
    const inputs = view.children.inputs;
    result.push(<Row id={inputs.start} keyword={inputs.keyword} actions={actions} state={state} />)

    for(let input of Object.values(inputs.children).sort((e1, e2) => e1.start - e2.start)) {
      result.push(<Input view={view} node={input} actions={actions} state={state} />)
    }

    return result
  }
}