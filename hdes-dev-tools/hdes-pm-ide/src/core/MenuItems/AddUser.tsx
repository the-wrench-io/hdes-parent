import React from 'react';
import { makeStyles, useTheme, Theme } from '@material-ui/core/styles';

import FormControl from '@material-ui/core/FormControl';
import FormLabel from '@material-ui/core/FormLabel';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import Checkbox from '@material-ui/core/Checkbox';

import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';


type AddUserProps = {
  open: boolean,
  handleClose: () => void
};

interface Project {
  id: string;
  name: string;
  checked: boolean;
}


function createData(id: string, name: string, checked: boolean): Project {
  return { id, name, checked };
}

const rows = [
  createData('0', 'scoring project', false),
  createData('1', 'casco pricing project', true),
  createData('2', 'risk project', false),
  createData('2', 'risk project', false),
  createData('2', 'risk project', false),
  createData('2', 'risk project', false),
  createData('2', 'risk project', false),
  createData('3', 'room condition', false)
];


const useStyles = makeStyles((theme) => ({
  tableLable: {
    marginTop: theme.spacing(3)
  },
  container: {
    maxHeight: 200,
  },
}));



const AddUser: React.FC<AddUserProps> = ({open, handleClose}) => {
  
  const classes = useStyles();
  const [value, setValue] = React.useState(0);
  
  
  const handleChange = (event: React.ChangeEvent<{}>, newValue: number) => {
    setValue(newValue);
  };

  const handleChangeIndex = (index: number) => {
    setValue(index);
  };
  
  return <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Add New User</DialogTitle>
        <DialogContent>
          
          <DialogContentText>
            To add new user to project(s), please enter name and external id(optional).
          </DialogContentText>
          
          <TextField autoFocus margin="dense" id="name" label="User name" type="text" fullWidth/>
          <TextField margin="dense" id="externalId" label="External ID" type="text" fullWidth />
          
          
          <Tabs
            variant="fullWidth"
            value={value}
            indicatorColor="primary"
            textColor="primary"
            onChange={handleChange}
            aria-label="disabled tabs example">
            
            <Tab label="Projects"/>
            <Tab label="OR" disabled />
            <Tab label="Groups" />
            
          </Tabs>
          
          <FormControl fullWidth>
            
            <TableContainer className={classes.container}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                      <TableCell></TableCell>
                      <TableCell></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {rows.map((row, id) =>
                    <TableRow hover role="checkbox" key={id}>
                      <TableCell padding="checkbox">
                        <Checkbox checked={row.checked} />
                      </TableCell>
                      <TableCell>{row.name}</TableCell>
                    </TableRow>)
                  }
                </TableBody>
              </Table>
            </TableContainer>
          </FormControl>
        </DialogContent>
        
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleClose} color="primary">
            Add
          </Button>
        </DialogActions>
      </Dialog>
  
}

export default AddUser;