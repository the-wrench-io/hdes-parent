import { Backend } from '../Backend';

enum ServiceActionType {
  setListeners
}

interface ServiceAction {
  type: ServiceActionType;
  
  setListeners?: Backend.ServiceListeners;
}

const serviceActions = {
  setData: (setListeners: Backend.ServiceListeners) => ({ type: ServiceActionType.setListeners, setListeners }),
}


const serviceReducer = (state: Backend.Service, action: ServiceAction): Backend.Service => {
  switch (action.type) {
    case ServiceActionType.setListeners: {
      if(!action.setListeners) {
        console.error("Action data error", action);
        return state;
      }
      return state.withListeners(action.setListeners);
    }
  }
}

export type { ServiceAction }
export { serviceActions, serviceReducer };
