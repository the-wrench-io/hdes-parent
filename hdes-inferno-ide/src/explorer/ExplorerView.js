import { Component } from 'inferno'
import { Section } from './Section'
import { SectionTabs } from './SectionTabs'


export class ExplorerView extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    const key = ['explorer'];
    return !this.props.state.getIn(key).equals(nextProps.state.getIn(key))
  }
  render() {
    const { actions, state } = this.props;
    return (
      <aside class='menu explorer-ed'>
        <ul class='menu-list'>
          <li class='explorer-title'>explorer</li>
          <SectionTabs actions={actions} state={state} type='oe' name='open editors' />
          <Section actions={actions} state={state} type='fl' name='flows' />
          <Section actions={actions} state={state} type='tg' name='tags' />
          <Section actions={actions} state={state} type='dt' name='decision tables' />
          <Section actions={actions} state={state} type='st' name='service tasks' />
          <Section actions={actions} state={state} type='mt' name='manual tasks' />
          <Section actions={actions} state={state} type='us' name='users' />
        </ul>
      </aside>
    );
  }
}