import React from 'react';
import ReactDOM from 'react-dom';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

import { Resources } from './core/Resources';

import App from './App';


import reportWebVitals from './reportWebVitals';


const theme = createMuiTheme({
  palette: {
    type: 'dark',
    primary: {
      main: '#41ead4',
    },
    secondary: {
      main: '#fbff12',
    },
    error: {
      main: '#ff206e',
    },
    background: {
      default: '#0c0f0a',
    },
  },

  props: {
    MuiPaper: {
      square: true,
    },
  },
  
  overrides: {
    MuiButton: {
      root: {
        borderRadius: 0,
      },
    },
    
    MuiDialogTitle: {
      root: {
        borderTop: '1px solid #fbff12',
        borderRight: '1px solid #fbff12',
        borderLeft: '1px solid #fbff12'
      }
    },
    MuiDialogActions: {
      root: {
        borderLeft: '1px solid #fbff12',
        borderRight: '1px solid #fbff12',
        borderBottom: '1px solid #fbff12'
      }
    },
    
    MuiCssBaseline: {
      '@global': {
        '*::-webkit-scrollbar': {
          width: '0.4em'
        },
        '*::-webkit-scrollbar-track': {
          '-webkit-box-shadow': 'inset 0 0 6px rgba(0,0,0,0.00)'
        },
        '*::-webkit-scrollbar-thumb': {
          backgroundColor: '#41ead4',
          outline: '1px solid slategrey'
        }
      }
    }
  }
});


const config = {};

ReactDOM.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <Resources.Provider config={config} >
        <App />
      </Resources.Provider>
    </ThemeProvider>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
