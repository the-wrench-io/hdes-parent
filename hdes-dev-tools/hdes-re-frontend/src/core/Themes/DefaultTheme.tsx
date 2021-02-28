import { createMuiTheme, Theme, ThemeOptions } from '@material-ui/core/styles';
import { Palette, PaletteOptions } from '@material-ui/core/styles/createPalette';

/*
--rich-black-fogra-39: #0c0f0aff;
--winter-sky: #ff206eff;
--lemon-glacier: #fbff12ff;
--turquoise: #41ead4ff;
--white: #ffffffff;

--acid-green: #c6ca53ff;
--orchid-crayola: #fab2eaff;
--uranian-blue: #a9def9ff;

*/
interface AssetPalette {
  assets: {
    fl: string,
    dt: string,
    st: string
  }
}

interface AppPaletteOptions extends PaletteOptions, AssetPalette {}
interface AppPalette extends Palette, AssetPalette {}

interface AppTheme extends Theme {
  palette: AppPalette;
}
interface AppThemeOptions extends ThemeOptions {
  palette: AppPaletteOptions;
}

const AppThemeOptions = {
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
    assets: {
      fl: '#c6ca53ff',
      dt: '#fab2eaff',
      st: '#a9def9ff'
    }
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
} as AppThemeOptions

const theme = createMuiTheme(AppThemeOptions);

export type { AppTheme }
export { theme, AppThemeOptions };
