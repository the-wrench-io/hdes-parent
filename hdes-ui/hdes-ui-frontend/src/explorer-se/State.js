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
import Immutable from 'immutable'

const ID = 'search'
const init = {
  filter: undefined,
  entryActive: undefined,
  entryActiveSection: undefined,
  entriesCollapsed: [],
  count: {entries: undefined, matches: undefined},
  entries: [
    // {id: '7', type: 'st', name: "uw-st1", matches: [{id: 'line 6', value: 'blablaa'}, {id: 'line 7', value: 'blablaa'}]},
  ]
}

const formatSearchResult = (criteria, entry) => {
  return {
    id: entry.type,
    value: entry.value
  }
}

const filter = (value, model) => {
  const entries = model.getIn(['explorer', 'entries']);
  let countEntries = 0;
  let countMatches = 0;
  const result = [];

  if(value && value.trim()) {
    const criteria = value.toLowerCase()
    for(let entry of entries.toJS()) {
      console.log(entry)
      const matches = []
      
      for(let search of entry.search) {

        if(search.value.toLowerCase().indexOf(criteria) > -1) {
          matches.push(formatSearchResult(criteria, search));
        }
      }

      if(matches.length > 0) {
        countEntries++;
        countMatches += matches.length;
        result.push({
          id: entry.id, 
          type: entry.type, 
          name: entry.name, 
          matches: matches})
      }
    }
  }
  
  return model
    .setIn([ID, 'filter'], value)
    .setIn([ID, 'entries'], Immutable.fromJS(result))
    .deleteIn([ID, 'entryActive'], value)
    .setIn([ID, 'entriesCollapsed'], Immutable.fromJS([]))
    .updateIn([ID, 'count'], value => value.set('entries', countEntries).set('matches', countMatches));
}

// all explorer actions
const actions = update => ({
  setSearchFilter: (value) => {
    update(model => filter(value, model))
  },
  toggleSearchEntry: (entryId) => {
    update(model => model.update(ID, 
      e => e
      .set('entryActive', entryId)
      .update('entriesCollapsed', v => {
        const contains = v.indexOf(entryId);
        return contains > -1 ? v.delete(contains) : v.push(entryId);
      })))
  },
  toggleSearchEntrySection: (entryId, sectionId) => {
    update(model => model.update(ID, 
      e => e.set('entryActive', sectionId)
    ))
  }
})

export const State = {
  id: ID,
  initial: init,
  actions: actions
}
