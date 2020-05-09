/*-
 * #%L
 * hdes-dev-app-ui
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import * as d3 from 'd3'

const createMatrix = (count, entry, byId) => {
  const result = [];

  for (let id of entry.deps.in) {
    const index = byId[id].index
    result[index] = 100;
  }
  for (let id of entry.deps.out) {
    const index = byId[id].index
    result[index] = 500;
  }

  let connections = false;
  for (let index = 0; index < count; index++) {
    if (!result[index]) {
      result[index] = 0;
    } else {
      connections = true;
    }
  }

  // Show connection to self
  if (!connections) {
    result[entry.index] = 100;
  }

  return result;
}

export const createChordData = (byId) => {
  // sort
  const byIndex = Object.values(byId).sort((e1, e2) => {
    const deps1 = e1.deps.count, deps2 = e2.deps.count;
    if (deps1 === 0 && deps2 !== 0) {
      return -1;
    } else if (deps2 === 0 && deps1 !== 0) {
      return 1;
    }
    const result = e1.src.label.localeCompare(e2.src.label)
    if (result === 0) {
      return e1.src.name.localeCompare(e2.src.name)
    }
    return result
  });

  // create index
  let index = 0;
  for (let entry of byIndex) {
    entry.index = index++;
  }

  const count = byIndex.length;
  const matrix = []
  for (let e of byIndex) {
    matrix[e.index] = createMatrix(count, e, byId);
  }
  return {
    byId: byId,
    byIndex: byIndex,
    matrix: matrix
  };
}

const getColor = (config, byIndex, index) => {
  const colors = config.colors;
  const simple = colors[index];
  if (simple) {
    return simple;
  }
  const entry = byIndex[index]
  const deps = entry.deps.count
  return colors[deps === 0 ? 'empty' : entry.src.label];
}

export const chord = ({element, data, onClick}) => {

  const onClickHandler = (d) => {
    const source = d.source.value;
    const target = d.target.value;
    let index 
    if(source > target) {
      index = d.source.index
    } else {
      index = d.target.index
    }
    onClick(data.byIndex[index])
  }

  
  const config = {
    colors: {
      empty: 'hsl(48, 100%, 67%)',
      st: 'hsl(273, 81%, 69%)',
      dt: 'hsl(171, 100%, 41%)',
      error: 'hsl(348, 100%, 61%)',
      fl: '',
      text: 'hsl(0, 0%, 96%)',
      textSep: 'hsl(0, 0%, 96%)',
    },
    width: 660,
    height: 800,
    center: {x: 340, y: 440},
    radius: 200,
    badAngle: 0.02,
    border: { margin: 5, width: 10 }
  }
  d3.select(element).select('svg').remove()

  // create the svg area
  const svg = d3.select(element)
    .append("svg")
    .attr("width", config.width)
    .attr("height", config.height)
    .append("g")
    .attr("transform", `translate(${config.center.x}, ${config.center.y})`) // center drawing

  // give this matrix to d3.chord(): it will calculates all the info we need to draw arc and ribbon
  var res = d3.chord()
    .padAngle(config.badAngle)     // padding between entities (black arc)
    .sortSubgroups(d3.descending)(data.matrix)

  // add the groups on the inner part of the circle
  const border = {
    innerRadius: config.radius + config.border.margin,
    outerRadius: config.radius + config.border.margin + config.border.width
  };

  const toggleFade = (d, enabled) => {
    d3.selectAll('.ribbons').classed('fade', enabled)
    d3.selectAll('.ribbon-' + d.source.index + '-' + d.target.index).classed('highlight', enabled)
  }

  const ribbonCenter = (d) => {
    const angle = (d.endAngle - d.startAngle) / 2
    return "rotate(" + ((d.startAngle + angle) * 180 / Math.PI - 90) + ") translate(" + border.outerRadius + ",0)";
  }

  // Add the links between groups
  svg
    .datum(res)
    .append("g")
    .attr('class', 'ribbons')
    .selectAll("path")
    .data((d) => d)
    .enter()
    .append("path")
    .attr("d", d3.ribbon().radius(config.radius))
    .attr('class', (d) => 'ribbon-' + d.source.index + '-' + d.target.index)
    .style("fill", d => getColor(config, data.byIndex, d.source.index))
    .style("stroke", "black")
    .on("mouseover", (d) => toggleFade(d, true))
    .on("mouseleave", (d) => toggleFade(d, false))
    .on("click", onClickHandler)

  // line groups
  const group = svg
    .datum(res)
    .append("g")
    .selectAll("g")
    .data((d) => d.groups);

  group
    .enter()
    .append("g")
    .append("path")
    .style("fill", "grey")
    .style("stroke", "black")
    .attr("d", d3.arc()
      .innerRadius(border.innerRadius)
      .outerRadius(border.outerRadius)
    )

  // middle sep lines
  group.enter()
    .append("g")
    .attr("transform", ribbonCenter)
    .append("line")               // By default, x1 = y1 = y2 = 0, so no need to specify it.
    .attr("x2", 100)
    .attr("stroke", getColor(config, data.byIndex, 'textSep'));

  // add text labels
  group
    .enter()
    .append("g")
    .attr("transform", ribbonCenter)
    .append("text")
    .attr("x", 20)
    .attr("y", -10)
    .attr("dy", ".35em")
    .attr("fill", getColor(config, data.byIndex, 'text'))
    .attr("transform", (d) => d.startAngle + (d.endAngle - d.startAngle) / 2 > Math.PI ? "rotate(180) translate(-60)" : null)
    .style("text-anchor", (d) => d.angle > Math.PI ? "end" : null)
    .text((d) => data.byIndex[d.index].src.name)
    .style("font-size", 10)
}
