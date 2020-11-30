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
import { Component } from 'inferno'
import { DebugSummary } from './../explorer-dg'


const active = (state, id) => {
  return state.getIn(['supportbar', id, 'enabled'])
}

const Icon = ({active, icon, onClick}) => {
  const color = active ? 'active' : '';
  const style = 'button is-fullwidth ' + color
  return <div class={style} onClick={onClick}><span class={`${icon} is-size-4 icon`} /></div>
}

export class Supportbar extends Component {
  //shouldComponentUpdate(nextProps, nextState) {
  //  const key = ['supportbar'];
  //  return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  //}
  
  render() {
    const { actions, state } = this.props

    let view;
    if(state.getIn(['supportbar', 'debug', 'enabled'])) {
      view = (<DebugSummary state={state} actions={actions} />)
    } else {
      return null
    }

    return (<div class='tile is-parent is-4 is-radiusless is-marginless is-paddingless supportbar'>
      <div class='tile is-child'>{view}</div>
    </div>)
  }
}
