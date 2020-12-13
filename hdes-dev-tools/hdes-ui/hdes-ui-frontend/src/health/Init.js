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


export class Init extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['health', 'init'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { state } = this.props;
    const log = state.getIn(['health', 'init', 'log']).map(l => <div class="log">{l}</div>).toJS()

    return (<div class='container init'>
      <section class="hero">
        <div class="hero-body">
          <div class="container">
            <h1 class="title">Loading...</h1>
            <h2 class="subtitle">
              { state.getIn(['health', 'init', 'loading']) ?
                (<progress class="progress is-large is-primary" max="100">100%</progress>) :
                (<progress class="progress is-large is-primary" max="100" value='100'/>)
              }
            </h2>
            {log}
          </div>
        </div>
      </section>
    </div>);
  }
}
