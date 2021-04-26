import { Tree } from '../Tree';

const _CACHE: Record<string, Tree.DimensionsTested> = {};

const fit = (value: number, limits: {min: number, max: number}) => {
  if(value < limits.min) {
    return limits.min;
  }
  if(value > limits.max) {
    return limits.max;
  }
  return value;
} 

const TextDimensions = (svg: Snap.Paper, text: string, attr: {}): Tree.DimensionsTested => {
  
  const cacheKey = text + JSON.stringify(attr);
  if(_CACHE[cacheKey]) {
    return _CACHE[cacheKey];
  }
  const temp = svg.text(0, 0, text);
  temp.attr(attr);
  
  const box = temp.getBBox();
  const dimensions = { 
    width:  box.width, 
    height: box.height
  };
  
  const avg = { width: Math.round(dimensions.width/text.length), height: dimensions.height };
  const result: Tree.DimensionsTested = { dimensions, avg };

  _CACHE[cacheKey] = result;
  return result;
}


interface TypographyDimensionsProps {
  words: Tree.Typography; 
  limits: { max: Tree.Dimensions, min: Tree.Dimensions };
  attr: {};  
}
const TypographyDimensions = (svg: Snap.Paper, props: TypographyDimensionsProps): Tree.DimensionsTested => {
  
  const cacheKey = props.words + JSON.stringify(props.attr);
  if(_CACHE[cacheKey]) {
    return _CACHE[cacheKey];
  }
  const text = props.words.text ? props.words.text : "";
  const temp = svg.text(0, 0, text);
  temp.attr(props.attr);
  
  const box = temp.getBBox();
  const dimensions = { 
    width:  fit(box.width,  {min: props.limits.min.width,   max: props.limits.max.width}), 
    height: fit(box.height, {min: props.limits.min.height,  max: props.limits.max.height})
  };
  
  const avg = { width: dimensions.width/text.length, height: dimensions.height };
  const result: Tree.DimensionsTested = { dimensions, avg };

  _CACHE[cacheKey] = result;
  return result;
}

export { TypographyDimensions, TextDimensions };
export type { TypographyDimensionsProps };

