import { Component } from 'inferno';


const getConnection = (state) => {
  const connection = state.getIn(['health', 'connection'])
  if(connection) {
    return <div></div>
  }
  return <div>
    <span class='icon'><i class='fas fa-skull-crossbones is-size-6' /></span>
    <span>lost connection</span>
  </div>
}

const getTag = (state) => {
  return <div>
    <span class='icon'><i class='fas fa-code-branch is-size-6 icon-small' /></span>
    <span>master</span>
  </div>
}

const getColor = (state) => {
  const connection = state.getIn(['health', 'connection'])
  return connection ? 'has-background-info' : 'has-background-danger'
}

export class Healthbar extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['health'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key));
  }
  render() {
    const { state } = this.props;
    return (<div class={getColor(state)}>
        <nav class="level is-ide-info-contents">
          <div class="level-left">
            <div class="level-item">{getTag(state)}</div>
            <div class="level-item">{getConnection(state)}</div>
          </div>
          <div class="level-right">
            <div class="level-item">
              <strong>All</strong>
            </div>
          </div>
        </nav>
      </div>
    );
  }
}