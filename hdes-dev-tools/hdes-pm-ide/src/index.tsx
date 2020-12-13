import React from 'react';
import ReactDOM from 'react-dom';

import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';


import App from './App';
import reportWebVitals from './reportWebVitals';


const theme = createMuiTheme({
  palette: {
    type: 'dark'
  },
  overrides: {
    MuiCssBaseline: {
      '@global': {
        '*::-webkit-scrollbar': {
          width: '0.4em'
        },
        '*::-webkit-scrollbar-track': {
          '-webkit-box-shadow': 'inset 0 0 6px rgba(0,0,0,0.00)'
        },
        '*::-webkit-scrollbar-thumb': {
          backgroundColor: 'black',
          outline: '1px solid slategrey'
        }
      }
    }
  }
});


ReactDOM.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <App />
    </ThemeProvider>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
