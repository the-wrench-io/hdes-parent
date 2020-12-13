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
