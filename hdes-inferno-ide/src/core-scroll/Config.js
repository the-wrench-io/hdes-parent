
export const Config = ({ id, total, elementSize, top, dimensions }) => {
  let containerSize = Math.max(top ? dimensions.height : dimensions.width, elementSize)
  const page = Math.min(total, Math.floor(containerSize/elementSize))

  const config = {
    id: id,
    content: { size: total * elementSize, style: undefined },
    container: { size: containerSize, style: undefined },

    element: { size: elementSize },
    page: { size: page * elementSize, total: page },
    pages: { total: Math.ceil(total/page), entries: [] },
    total: total,
    next: undefined,
    extra: 0
  }
  if(containerSize > config.content.size) {
    config.extra = containerSize - config.page.size
    config.content.size = containerSize;
    config.page.size = containerSize;
  }
  let viewStyle
  let pageStyle
  if(top) {
    viewStyle = (index) => { return { 
      height: config.element.size + 'px'
    } }
    pageStyle = (index) => { return { 
      position: 'absolute', 
      top: config.page.size*index + 'px', 
      height: config.page.size + 'px' }}
    config.next = (current) => Math.ceil(current.scrollTop/config.page.size)
    config.content.style = { height: config.content.size + 'px' }
    config.container.style = { 
      'max-height': config.container.size + 'px',
      'overflow-y': 'auto',
      'position': 'relative'
    }
  } else {
    viewStyle = (index) => { return {
      display: 'inline-block',
      width: (index === total-1 ? config.element.size + config.extra : config.element.size) + 'px' 
    }}
    pageStyle = (index) => { return { 
      position: 'absolute',
      left: config.page.size*index + 'px', 
      width: config.page.size + 'px'} }
    
    config.next = (current) => Math.ceil(current.scrollLeft/config.page.size)
    config.content.style = { width: config.content.size + 'px', 'min-height': '100px'}
    config.container.style = {
      'max-width': config.container.size + 'px',
      'overflow-x': 'auto',
      'position': 'relative'
    }
  }


  for(let index = 0; index < config.pages.total; index++) {
    const view = [];
    const end = Math.min((index + 1) * config.page.total, total)
    const start = index * config.page.total;
    for(let id = start; id < end; id++) {
      view.push({ id: id, style: viewStyle(id) })
    }

    const endPos = Math.max(end*elementSize, config.page.size);
    const page = {
      id: index + 1, 
      view: view,
      style: pageStyle(index),
      start: { id: start, pos: start*elementSize }, 
      end: { id: end - 1, pos: endPos } };
    config.pages.entries.push(page)
  }

  //console.log(config)
  return config;
}