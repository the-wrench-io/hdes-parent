
import Backend from './../Backend'; 


interface Store {
  config: Backend.ServerConfig;
  setErrors: (value: Backend.ServerError | string, url: string, request: RequestInit) => void;
  onSave: (resource: Backend.AnyResource) => void;
  fetch<T>(url: string, init?: RequestInit): Promise<T>;
}

class ServerStore implements Store {
  private _config: Backend.ServerConfig;
  private _onSave: (resource: Backend.AnyResource) => void;
  private _onError: (error: Backend.ServerError) => void;

  constructor(
    onSave: (resource: Backend.AnyResource) => void,
    onError: (error: Backend.ServerError) => void, 
    config: Backend.ServerConfig) {
    
    this._onSave = onSave;
    this._onError = onError;
    this._config = config;
  }
  
  get config() {
    return this._config;
  }
  
  onSave(resource: Backend.AnyResource) {
    this._onSave(resource);
  }
  
  fetch<T>(url: string, init?: RequestInit): Promise<T> {
    if(!url) {
      throw new Error("can't fetch with undefined url")
    }
    return fetch(url, init)
      .then(response => {
        if (!response.ok) {
          throw new Error(response.statusText)
        }
        return response.json()
      })
      .catch(errors => this.setErrors(errors, url, init))
      .then(data => {
        const method = init?.method;
        if(method === "POST" || method === "PUT" || method === "DELETE") {
          this.onSave(data)
        }
        return data ? data : []
      }) 
  }
  
  setErrors(value: any, url: string, init?: RequestInit) {
    console.error(value, url, init)
  }
}

export type { Store };
export { ServerStore };
