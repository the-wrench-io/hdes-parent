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