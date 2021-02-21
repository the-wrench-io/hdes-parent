import { Backend } from '../Backend';

enum ServiceReducerActionType {
  setListeners
}

interface ServiceReducerAction {
  type: ServiceReducerActionType;
  setListeners?: Backend.ServiceListeners;
}

const ServiceReducer = (state: Backend.Service, action: ServiceReducerAction): Backend.Service => {
  switch (action.type) {
    case ServiceReducerActionType.setListeners: {
      if(!action.setListeners) {
        console.error("Action data error", action);
        return state;
      }
      return state.withListeners(action.setListeners);
    }
  }
}

export type { ServiceReducerAction }
export { ServiceReducer, ServiceReducerActionType };
