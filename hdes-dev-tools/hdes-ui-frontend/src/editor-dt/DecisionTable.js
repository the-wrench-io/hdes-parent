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
import { Cells, Builders } from './components'
import { Scroll } from './../core-scroll'

const config = {
  title: 'title is-6'
}

export class DecisionTable extends Component {

  shouldComponentUpdate(nextProps, nextState) {
    return true
  }

  render() {
    const { view, editable, onCellEdit, onChangeStart, onChangeCancel } = this.props
    const activeMode = 'delete'
    let mode;
    if(activeMode === 'delete') {
      mode = (children) => {return (
        <div class='is-delete'>
          <span class='icon is-small'><i class='las la-times'></i></span>
          {children}
        </div>)}
    }
    const columns = <Scroll id={view.value.name}
      size={{ element: 200 }}
      activeItem={editable.id ? editable.id : null}
      count={view.value.rows.length}
      renderPage={({ref, value, children}) => <div ref={ref} style={value.style}>{children}</div>}
      renderItem={({id, style}) => <div class='scroll-item column is-gapless' style={style}><Cells.Ruleset mode={mode} id={id} editable={editable} onClick={onCellEdit} config={config} node={view.value.rows[id]} view={view} /></div> } 
      renderScroll={({ref, style, onscroll, children}) => <div class='scroll-page' style={style.container} ref={ref} onscroll={onscroll}><div style={style.children}>{children}</div></div> }/>

    return (<div class='columns is-editor'>
      <div class='column'>

        <Builders.Generic view={view} editable={editable} onChangeStart={onChangeStart} onChangeCancel={onChangeCancel}/>
        
        <div class='columns config'>
          <div class='column'>
            <nav class='level'>
              <div class='level-left'>
                <div class='level-item'>
                  <Cells.Name config={config} view={view}/>
                </div>
                <div class='level-item'>
                  <Cells.Search config={config} view={view}/>
                </div>
              </div>
              <div class='level-right'>
                <p class='level-item'><Cells.HitPolicy config={config} view={view}/></p>
              </div>
            </nav>
          </div>
        </div>

        <div class='columns highlight'>
          <div class='column is-gapless is-one-fifth'>
            <Cells.Conditions mode={mode} config={config} nodes={view.value.headers.filter(h => h.direction === 'IN')}/>
            <Cells.Outcomes mode={mode} config={config} nodes={view.value.headers.filter(h => h.direction !== 'IN')}/>
          </div>
          <div class='column is-four-fifth rulesets'>{columns}</div>
        </div>

      </div>
    </div>)
  }
}
