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


const getDef = (state) => {
  const explorer = state.getIn(['explorer'])
  const entryOpen = explorer.get('entryOpen')
  if(!entryOpen) {
    return null
  }
  
  return explorer.get('entries').filter(e => e.get('id') === entryOpen).toJS()[0]
}

const getSummary = (state, def) => {
  if(!def) {
    return null
  }
  return state.getIn(['debug', 'outputs', def.id])
}

export class DebugSummary extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key0 = ['debug']
    const key1 = ['explorer']
    return !this.props.state.getIn(key0).equals(nextProps.state.getIn(key0)) || 
      !this.props.state.getIn(key1).equals(nextProps.state.getIn(key1));
  }

  getValue(summary) {
    const result = []
    
    if(!summary) {
      return result
    } else if(!summary.output) {
      result.push(<div class='notification is-primary'>Nothing was generated into output</div>)
    } else {
      result.push(<li class='explorer-subtitle'>Total run time: {summary.output.meta.time} ms</li>)
      result.push(<li class='explorer-subtitle'>JSON output: </li>)
      result.push(<li class='output'>{JSON.stringify(summary.output.value, undefined, 2)}</li>)

    }
    return result
  }

  render() {
    const { actions, state } = this.props;
    const def = getDef(state)
    const summary = getSummary(state, def)
    return (
      <aside class='debug'>
        <ul class='menu-list'>
          <li class='explorer-title' onClick={() => actions.debug.closeSummary(def)} >
            <a href={'#debugSummary/'}><i class='is-close icon is-small has-text-right las la-times'></i><span>debug summary</span></a>  
          </li>
          {this.getValue(summary)}
        </ul>
      </aside>
    );
  }
}
