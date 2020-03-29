import { Component, createRef } from 'inferno'
import { Config } from './Config'
import lodash from 'lodash';


class Page extends Component {
  constructor(props) {
    super(props);
    this.observable = createRef()
  }

  render() {
    const { value, renderItem, renderPage } = this.props
    const children = value.view.map(renderItem)
    return renderPage({ ref: this.observable, children: children, value: value })
  }
}

export class Scroll extends Component {
  constructor(props) {
    super(props);
    const {
        // activeItem
        // size, // { element: 60, container: 500 } element and container size, height or width 
        // count,// total elements 
        top,  // top or left scrollbar
        // renderItem, // callback ({id, style}) => <div style={style}>{id}</div>
        // renderPage,  // callback ({ref, value, children}) => <div ref={ref} style={value.style}>{children}</div>
        // renderScroll, // callback ({ref, style, onScroll, children}) => <div class='scroll' ref={ref} onscroll={onscroll}><div style={config.content.style}>{children}</div></div>
        } = props

    this.setState = this.setState.bind(this)
    this.handleScroll = this.handleScroll.bind(this)
    this.handleUpdate = this.handleUpdate.bind(this)

    this.scrollable = createRef()
    this.props.top = top !== undefined && top !== false ? true : false
    this.state = { config: {} }

  }
  componentDidMount() {
    this.handleUpdate()
  }
  shouldComponentUpdate(nextProps, nextState) {
    return (
      this.props.activeItem !== nextProps.activeItem ||
      this.props.id !== nextProps.id ||
      this.state.config.total !== nextState.config.total || 
      this.state.start !== nextState.start || 
      this.state.end !== nextState.end)
  }
  componentDidUpdate(prevProps) {
    if(this.props.id !== prevProps.id) {
      this.handleUpdate()
    }
  }
  handleUpdate() {
    const { current } = this.scrollable
    if(current) {
      const dimensions = {width: current.offsetWidth, height: current.offsetHeight}
      this.setState((state, props) => {
        const config = Config({
          id: props.id,
          top: props.top,
          total: props.count, 
          elementSize: props.size.element, 
          containerSize: props.size.container,
          dimensions: dimensions})
        const value = this.find(config, this.scrollable)
        return { config: config, view: value.values, start: value.start, end: value.end }
      })
    }
  }

  find(config, scrollable) {
    const values = [];
    const { current } = scrollable
    const target = config.next(current)
    const start = Math.max(target - 2, 0);
    const end = Math.min(start + 2, config.pages.entries.length - 1);
    for(let index = start; index <= end; index++) {
      values.push(config.pages.entries[index])
    }
    return { values, start, end };
  }

  handleScroll(e) {
    this.setState((state, props) => {
      const value = this.find(state.config, this.scrollable)
      return { config: state.config, view: value.values, start: value.start, end: value.end }
    })
  }
  
  render() {
    const { current } = this.scrollable

    // init
    if(!current) {
      return <div class='scroll-measure' style={{height: '100%', width: '100%'}} ref={this.scrollable}>Loading...</div>
    }

    const { config, view } = this.state
    const { renderItem, renderPage, renderScroll, activeItem } = this.props

    const children = view.map(e => <Page key={e.id} config={config} value={e} activeItem={activeItem}
      renderPage={renderPage} 
      renderItem={renderItem} />)

    const onscroll = lodash.throttle(this.handleScroll, 1000)
    const style = { container: config.container.style, children: config.content.style }
    const ref = this.scrollable

    return renderScroll({onscroll, style, ref, children})

/*
    const elements = new Array(1000).fill(0);
    return <Scroll 
      top size={{element: 60, container: 500}}
      //top={false} size={{element: 100, container: 500}}
      count={elements.length}
      renderPage={({ref, value, children}) => <div ref={ref} style={value.style}>{children}</div>}
      renderItem={({id, style}) => <div class='boxx' style={style}>{id}</div> } 
      renderScroll={({ref, style, onscroll, children}) => <div style={style.container} ref={ref} onscroll={onscroll}><div style={style.children}>{children}</div></div> }/>
*/
  }
}