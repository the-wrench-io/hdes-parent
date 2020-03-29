import { State }  from './State'
import { createBackendService }  from './BackendService'

export const createBackendState = config => (actions) => State(actions, createBackendService(config));