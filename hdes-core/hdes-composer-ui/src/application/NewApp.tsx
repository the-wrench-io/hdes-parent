import React from 'react';

import { IntlProvider } from 'react-intl';
import { ThemeProvider, StyledEngineProvider } from '@mui/material/styles';
import { SnackbarProvider } from 'notistack';
import Burger, { siteTheme } from '@the-wrench-io/react-burger';
import Client, { messages, Main, Secondary, Toolbar, Composer } from '@the-wrench-io/hdes-ide';


declare global {
  interface Window {
    _env_: {
      url?: string,
      csrf?: Csrf,
    }
  }
}

interface Csrf {
  key: string, value: string
}


const getUrl = () => {
  if (window._env_ && window._env_.url) {
    const url = window._env_.url;
    if (url.endsWith("/")) {
      return url.substring(0, url.length - 1)
    }
    return url;
  }

  return "http://localhost:8081/assets";
}

console.log("WINDOW CONFIG", window._env_);

const init = {
  locale: 'en',
  url: getUrl(),
  csrf: window._env_?.csrf
};

console.log("INIT", init);


const store: Client.Store = {
  fetch<T>(path: string, req?: RequestInit): Promise<T> {
    if (!path) {
      throw new Error("can't fetch with undefined url")
    }

    const defRef: RequestInit = {
      method: "GET",
      credentials: 'same-origin',
      headers: {
        "Content-Type": "application/json;charset=UTF-8"
      }
    };

    if (init.csrf) {
      const headers: Record<string, string> = defRef.headers as any;
      headers[init.csrf.key] = init.csrf.value;
    }

    const url = init.url;
    const finalInit: RequestInit = Object.assign(defRef, req ? req : {});


    return fetch(url + path, finalInit)
      .then(response => {
        if (response.status === 302) {
          return null;
        }
        if (!response.ok) {
          return response.json().then(data => {
            console.error(data);
            throw new Client.StoreError({
              text: response.statusText,
              status: response.status,
              errors: data
            });
          });
        }
        return response.json();
      })
  }
};


const CreateApps: React.FC<{}> = () => {
  // eslint-disable-next-line 
  const backend = React.useMemo(() => new Client.ServiceImpl(store), [store]);
  const wrenchComposer: Burger.App<Composer.ContextType> = {
    id: "wrench-composer",
    components: { primary: Main, secondary: Secondary, toolbar: Toolbar },
    state: [
      (children: React.ReactNode, restorePoint?: Burger.AppState<Composer.ContextType>) => (<>{children}</>),
      () => ({})
    ]
  };

  return (
    <Composer.Provider service={backend}>
      <Burger.Provider children={[wrenchComposer]} secondary="toolbar.assets" drawerOpen/>
    </Composer.Provider>
  )
}

const NewApp = (
  <IntlProvider locale={init.locale} messages={messages[init.locale]}>
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={siteTheme}>
        <SnackbarProvider>
          <CreateApps />
        </SnackbarProvider>
      </ThemeProvider>
    </StyledEngineProvider>
  </IntlProvider>);

export default NewApp;









