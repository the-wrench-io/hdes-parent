import { Component } from 'inferno';

export const Search = ({view, config}) => {
  return (<div class='is-entry'>
    <div class={'is-inline ' + config.title}>Search: </div>
    <div class='is-inline'>...</div>
  </div>)
}

export const Name = ({view, config}) => {
  return (<div class='is-entry'>
    <div class={'is-inline ' + config.title}>Name: </div>
    <div class='is-inline'>{view.value.name}</div>
  </div>)
}

export const HitPolicy = ({view, config}) => {
  return (<div class='is-entry'>
    <div class={'is-inline ' + config.title}>Hit policy: </div>
    <div class='is-inline'>{view.value.hitPolicy}</div>
  </div>)
}

export const Conditions = ({nodes, config, mode}) => {
  return (<div class='conditions'>
    <div class={config.title}>Conditions</div>
    {nodes.map(n => <div class='entry header'>{mode ? mode(n.name) : n.name}</div>)}
  </div>)
}

export const Outcomes = ({nodes, config, mode}) => {
  return (<div class='outcomes'>
    <div class={config.title}>Outcomes</div>
    {nodes.map(n => <div class='entry header'>{mode ? mode(n.name) : n.name}</div>)}
  </div>)
}

export class Ruleset extends Component {

  render() {
    const {id, node, view, config, editable, onClick, mode} = this.props
    const { headers } = view.value;
    const conditions = []
    const outcomes = []
  
    for(let index = 0; index < node.cells.length; index++) {
      const header = headers[index];
      const cell = node.cells[index]
      const active = editable.id ? editable.id.id === cell.id : false;
      
      const style = active ? ' active': '';
      const entry = <div onClick={() => onClick({id: cell.id, row: id, cell: index})} class={'entry' + style}>{cell.value}</div>
  
      if(header.direction === 'IN') {
        conditions.push(entry)
      } else {
        outcomes.push(entry)
      }
    }
    const order = '#' + node.order;
    const value = mode ? mode(order) : order
    return [
      <div class='conditions'>
        <div class={config.title}>{value}</div>
        {conditions}
      </div>,
  
      <div class='outcomes'>
        <div class={config.title}>{order}</div>
        {outcomes}
      </div>]
  }
}