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
export const createBackendService = (config) => new BackendService(config)

class BackendService {
  
  constructor(config) {
    console.log('Backend init', config.toJS());
    this.config = config;
    this.csrf = config.get('csrf') ? config.get('csrf').toJS() : null
    this.readHeaders = readHeaders(this.csrf)
    this.writeHeaders = writeHeaders(this.csrf)
  }
  
  models(successHandler, errorHandler) {
    // console.log('loading models')
    const method = { method: 'GET', credentials: 'same-origin', headers: this.readHeaders }
    const url = this.config.get('url') + '/defs'
    return fetch(url, method)
      .then(getResponse)
      .then(successHandler)
      .catch(errors => getErrors(errors, errorHandler, url))
  }
  health(successHandler, errorHandler) {
    // console.log('loading health')
    const method = { method: 'GET', credentials: 'same-origin', headers: this.readHeaders }
    const url = this.config.get('url') + '/status'
    return fetch(url, method)
      .then(getResponse)
      .then(successHandler)
      .catch(errors => getErrors(errors, errorHandler, url))
  }
  create(entities, successHandler, errorHandler) {
    const map = (e) => {
      return { label: e.type, values: [{type: 'SET_NAME', value: e.name}]};
    }
    const body = Array.isArray(entities) ? entities.map(map) : [map(entities)];

    console.log(body)
    const method = { method: 'POST', body: JSON.stringify(body), credentials: 'same-origin', headers: this.writeHeaders }
    const url = this.config.get('url') + 'changes'
    return fetch(url, method)
      .then(getResponse)
      .then(successHandler)
      .catch(errors => getErrors(errors, errorHandler, url))
  }
  delete(entities, successHandler, errorHandler) {

    console.log(entities)
    const map = (e) => {
      return { id: e.id, rev: e.rev, label: e.label, values: [{type: 'DELETE'}]};
    }
    
    const body = Array.isArray(entities) ? entities.map(map) : [map(entities)];

    console.log(body)
    const method = { method: 'POST', body: JSON.stringify(body), credentials: 'same-origin', headers: this.writeHeaders }
    const url = this.config.get('url') + 'changes'
    return fetch(url, method)
      .then(getResponse)
      .then(successHandler)
      .catch(errors => getErrors(errors, errorHandler, url))
  }
}

function getResponse(response) {
  if (response.ok) {
    return response.json();
  }
  throw new FetchError(response.json());
}

function getErrors(error, callback, url) {
  if(!callback) {
    callback = (e) => console.error(e);
  }
  if(error.response) {
    error.response
    .then(data => {
      const messages = data.values.map(e => {
        return {id: e.id, defaultMessage: e.value, logCode: data.logCode, args: e.args};
      });
      callback(messages);
    })
  } else {
    console.error(error)
    callback([{id: 'errors.connection', defaultMessage: error.message, values: {reason: error.message, url: url}}]);
  }
}

function readHeaders(csrf) {
  let headers = {};

  if(csrf) {
    headers[csrf.key] = csrf.value;
  }
  return headers
}

function writeHeaders(csrf) {
  let headers = {
    Accept: 'application/json',
    'Content-Type': 'application/json; charset=utf-8'
  };

  if(csrf) {
    headers[csrf.key] = csrf.value;
  }
  return headers
}

class FetchError extends Error {
  constructor(response, status, ...params) {
    // Pass remaining arguments (including vendor specific ones) to parent constructor
    super(...params);

    // Maintains proper stack trace for where our error was thrown (only available on V8)
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, FetchError);
    }

    // Custom debugging information
    this.response = response;
  }
}
