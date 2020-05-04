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

export class Generic extends Component {


 //console.log(view)
// console.log(editable)

  render() {
    const { view, editable, onChangeStart, onChangeCancel } = this.props;

    let value;
    if(editable.id) {
      const cell = view.value.rows[editable.id.row].cells[editable.id.cell]
      value = cell.value
    }

    let style = ''
    if(editable.enabled) {
      style = ' active'
    }

    return (<div class={'columns editbar'}>
      <div class='column'>
        <div class={'field' + style}>
          <div class='icon is-size-4'><i class='las la-edit'></i></div>
          <div class='cell-editor' spellcheck='false' contenteditable='true' 
            onfocusout={() => onChangeCancel(editable)} 
            onfocus={() => onChangeStart(editable)} >{value}</div>
        </div>
      </div>
    </div>)
  }
} 
