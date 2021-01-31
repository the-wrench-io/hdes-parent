
import Backend from './../Backend'; 


interface Store {
  config: Backend.ServerConfig;
  setErrors: (value: Backend.ServerError | string, url: string, request: RequestInit) => void;
  setUpdates: () => void;
  fetch<T>(url: string, init?: RequestInit): Promise<T>;
}

class ServerStore implements Store {
  private _config: Backend.ServerConfig;
  setUpdates: () => void;

  constructor(setUpdates: () => void, config: Backend.ServerConfig) {
    this.setUpdates = setUpdates;
    this._config = config;
  }
  
  get config() {
    return this._config;
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
        if(method === "POST" || method === "PUT" || method === "POST") {
          this.setUpdates()
        }
        return data
      }) 
  }
  
  setErrors(value: Backend.ServerError | string, url: string, init?: RequestInit) {
    console.error(value, url, init)
  }
}

export type { Store };
export { ServerStore };
