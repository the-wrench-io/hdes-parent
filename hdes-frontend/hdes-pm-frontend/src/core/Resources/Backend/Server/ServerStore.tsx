
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
  private _onDelete: (resource: Backend.AnyResource) => void;
  private _onError: (error: Backend.ServerError) => void;

  constructor(
    onSave: (resource: Backend.AnyResource) => void,
    onDelete: (resource: Backend.AnyResource) => void,
    onError: (error: Backend.ServerError) => void, 
    config: Backend.ServerConfig) {
    
    this._onSave = onSave;
    this._onDelete = onDelete;
    this._onError = onError;
    this._config = config;
  }
  
  get config() {
    return this._config;
  }
  onSave(resource: Backend.AnyResource) {
    this._onSave(resource);
  }
  onDelete(resource: Backend.AnyResource) {
    this._onDelete(resource);
  }
  fetch<T>(url: string, init?: RequestInit): Promise<T> {
    if(!url) {
      throw new Error("can't fetch with undefined url")
    }
    
    const finalInit = init ? init : {};
    return fetch(url, finalInit)
      .then(response => {
        if (!response.ok) {
          throw new Error(response.statusText)
        }
        return response.json()
      })
      .catch(errors => this.setErrors(errors, url, init))
      .then(data => {
        const method = init?.method;
        if(method === "POST" || method === "PUT") {
          this.onSave(data);
        } else if(method === "DELETE") {
          this.onDelete(data);
        }
        return data ? data : [];
      }) 
  }
  
  setErrors(value: any, url: string, init?: RequestInit) {
    console.error(value, url, init)
  }
}

export type { Store };
export { ServerStore };
