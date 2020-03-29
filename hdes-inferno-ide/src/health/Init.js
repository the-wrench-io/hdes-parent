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