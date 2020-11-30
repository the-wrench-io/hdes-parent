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

import { EditorView, EditorTabs } from './editor';
import { Iconbar, IconbarView } from './iconbar';
import { Supportbar } from './supportbar';

import { Init, Healthbar } from './health';


export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = props.states();
    this.skippedFirst = false;
  }
  componentDidMount() {
    var setState = this.setState.bind(this);
    this.props.states.map(state => 
      this.skippedFirst ? setState(state) : this.skippedFirst = true);
  }

  render() {
    const state = this.state
    const { actions } = this.props

    // application init
    if(state.getIn(['health', 'init', 'enabled'])) {
      return (<div class='app'>
        <Init state={state} actions={actions} />
      </div>)
    }

    return (<div class="tile is-ancestor is-radiusless is-marginless is-paddingless app">
      <div class="tile">
        <div class="tile is-parent is-vertical is-radiusless is-marginless is-paddingless">
          <div class="tile is-parent is-radiusless is-marginless is-paddingless">
            
            <Iconbar state={state} actions={actions}/>
            <IconbarView state={state} actions={actions}/>

            <div class="tile is-parent is-radiusless is-marginless is-paddingless editor-window">
              <div class="tile is-child">
                <EditorTabs state={state} actions={actions}/>
                <EditorView state={state} actions={actions} />
              </div>
            </div>
            
            <Supportbar state={state} actions={actions} />
          </div>

          <div class="tile is-parent is-radiusless is-marginless is-paddingless is-ide-info">
            <div class="tile is-child">
              <Healthbar state={state} actions={actions}/>
            </div>
          </div>

        </div>
      </div>
    </div>)
  }
}
