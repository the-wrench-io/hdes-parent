import { Component } from 'inferno'


const getIndent = (indent) => {
  if(!indent) {
    return ''
  }

  switch(indent) {
    case 2: return ' line-indent-2'
    case 4: return ' line-indent-4'
    case 6: return ' line-indent-6'
    case 8: return ' line-indent-8'
    case 10: return ' line-indent-10'
    case 12: return ' line-indent-12'
    case 14: return ' line-indent-14'
    case 16: return ' line-indent-16'
    default: console.log ('not found: ', indent)
      return ''
  }
}

export class RowEdit extends Component {
  render() {
    const { id, value, keyword, indent } = this.props

    const keywordNode = (keyword ? 
      <div class={'field-label is-small line-keyword-edit' + getIndent(indent)}>
        <label class='label'><div spellcheck='false' contenteditable='true' class='content'>{keyword}</div>:</label>
      </div> : null)

    const valueNode = (value ?
      <div class='field-body'>
        <div class='field'>
          <div class='control line-value'><input spellcheck='false' class="input is-shadowless is-small" type="text" placeholder="Small sized input" defaultValue={value}/></div>
        </div>
      </div> : null)

    return (<li class='line'><a href={'#' + id} class='is-paddingless'><div class='field is-horizontal'>
        <div class='field-label is-small line-id'>
          <label class='label'>{id}:</label>
        </div>
        {keywordNode}{valueNode}
      </div></a></li>)
  }
}

export class RowBoolean extends Component {
  constructor(props) {
    super(props)
    this.state = { active: false }
  }
  render() {
    const { id, value, keyword, indent, state, actions } = this.props
    const keywordNode = (keyword ? 
      <div class={'field-label is-small line-keyword-edit' + getIndent(indent)}>
        <label class='label'><div spellcheck='false' contenteditable='true' class='content'>{keyword}</div>:</label>
      </div> : null)

    let style = 'dropdown line-dropdown'
    if(state.getIn(['editorfl', 'menu', 'active']) === id) {
      style += ' is-active'  
    }
    
    const valueNode =(<div class={style} onfocusout={() => actions.editorfl.unsetActive()} onClick={() => actions.editorfl.toggleActive(id)}>
      <div class='dropdown-trigger'>
        <button class='button is-small' aria-haspopup='true' aria-controls='dropdown-menu'>
          <span>{value}</span>
          <span class="icon">
            <i class="fas fa-angle-down" aria-hidden="true"></i>
          </span>
        </button>
      </div>
      <div class='dropdown-menu' id='dropdown-menu' role='menu'>
        <div class='dropdown-content'>
          <a href={'#' + id} class='dropdown-item'>true</a>
          <a href={'#' + id} class='dropdown-item'>false</a>
        </div>
      </div>
    </div>)

    return (<li class='line'><a href={'#' + id} class='is-paddingless'><div class='field is-horizontal'>
        <div class='field-label is-small line-id'>
          <label class='label'>{id}:</label>
        </div>
        {keywordNode}{valueNode}
      </div></a></li>)
  }
}

export class RowDropdown extends Component {
  render() {
    const { id, value, keyword, indent, options, state, actions } = this.props
    const keywordNode = (keyword ? 
      <div class={'field-label is-small line-keyword-edit' + getIndent(indent)}>
        <label class='label'><div spellcheck='false' contenteditable='true' class='content'>{keyword}</div>:</label>
      </div> : null)

    const dropdown = options.map(e => <a href={'#' + id} class="dropdown-item">{e.value}</a>);
    let style = 'dropdown line-dropdown'
    
    if(state.getIn(['editorfl', 'menu', 'active']) === id) {
      style += ' is-active'  
    }
    
    const valueNode =(<div class={style}>
      <div class='dropdown-trigger'>
        <button class='button is-small' aria-haspopup='true' aria-controls='dropdown-menu' onfocusout={() => actions.editorfl.unsetActive()} onClick={() => actions.editorfl.toggleActive(id)}>
          <span>{value}</span>
          <span class='icon'>
            <i class='fas fa-angle-down' aria-hidden='true'></i>
          </span>
        </button>
      </div>
      <div class='dropdown-menu' id='dropdown-menu' role='menu'>
        <div class='dropdown-content'>{dropdown}</div>
      </div>
    </div>)

    return (<li class='line'><a href={'#' + id} class='is-paddingless'><div class='field is-horizontal'>
        <div class='field-label is-small line-id'>
          <label class='label'>{id}:</label>
        </div>
        {keywordNode}{valueNode}
      </div></a></li>)
  }
}

export class Row extends Component {
  render() {
    const { id, value, keyword, indent } = this.props
    const keywordNode = (keyword ? 
      <div class={'field-label is-small line-keyword' + getIndent(indent)}>
        <label class='label'>{keyword}:</label>
      </div> : null)

    const valueNode = (value ?
      <div class='field-body'>
        <div class='field'>
          <div class='control line-value'><input spellcheck='false' class="input is-shadowless is-small" type="text" placeholder="Small sized input" defaultValue={value}/></div>
        </div>
      </div> : null)

    return (<li class='line'><a href={'#' + id} class='is-paddingless'><div class='field is-horizontal'>
        <div class='field-label is-small line-id'>
          <label class='label'>{id}:</label>
        </div>
        {keywordNode}{valueNode}
      </div></a></li>)
  }
}