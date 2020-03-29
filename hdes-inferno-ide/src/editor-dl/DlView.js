import { Component } from 'inferno'
import { chord, createChordData } from './chord'
import { data } from './data'


const Delete = ({entries, actions}) => {
  if(entries.size === 0) {
    return null
  }

  const list = entries.toJS()
  .map(e => <li><button class='delete' onClick={() => actions.editordl.unmark(e)}></button>{e.src.name}</li>)

  return (<div class='entries' >
    <ul>{list}</ul>
    <button class='button is-fullwidth' onClick={() => actions.editordl.delete(entries.toJS())}>confirm delete</button>
  </div>)
}

const Section = ({title, subtitle, children}) => {
  return (<div class='tile'>
    <section class='hero'>
      <div class='hero-body'>
        <div class='container'>
          <h1 class='title'>{title}</h1>
          <h2 class='subtitle'>{subtitle}</h2>
        </div>
      </div>
      {children}
    </section>
  </div>)
}


export class DlView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['editordl']
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))
  }

  svgElemeRef = (domNode) => this.svgElement = domNode

  componentDidUpdate() {
    const { actions, state } = this.props
    const chordData = state.getIn(['editordl', 'chord']);
    if(chordData) {
      chord({element: this.svgElement, data: chordData, onClick: (entry) => actions.editordl.mark(entry)});
    }
  }

  componentDidMount() {
    const { actions } = this.props
    const chordData = createChordData(data);
    actions.editordl.setChord(chordData)
  }

  render() {
    const { actions, state } = this.props
    //const active = state.getIn(['editordl', 'active'])
    const entries = state.getIn(['editordl', 'entries'])

    return (<div class='tile dl-editor'>
      <div class='tile'>
        <Section title='Dependency chord' subtitle='Click on the ribbon to mark for deletion'>
          <div class='chord' ref={this.svgElemeRef} />
        </Section>
        <Section title='Resources marked for deletion' 
          subtitle={entries.size === 0 ? 'No entries marked' : `${entries.size} entries marked for deletion`}>
          <Delete entries={entries} actions={actions} />
        </Section>
      </div>
    </div>)
  }
}