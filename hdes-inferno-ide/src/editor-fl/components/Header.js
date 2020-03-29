import { Component } from 'inferno'
import { Row } from './Row'


export class Header extends Component {
  render() {
    const { view } = this.props
    const result = [];
    result.push(<Row id={view.id.start} keyword={view.id.keyword} value={view.id.value} />);

    if(view.description) {
      result.push(<Row id={view.description.start} keyword={view.description.keyword} value={view.description.value} />);
    }

    //const description = <Row />
    return result
  }
}