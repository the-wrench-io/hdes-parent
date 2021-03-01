import React from 'react';

import { createStyles, Theme, makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import TextField from '@material-ui/core/TextField';
import AddIcon from '@material-ui/icons/Add';
import InputLabel from '@material-ui/core/InputLabel';

import { Backend } from '../Resources';
import { Resources } from '../Resources';

import { IconButtonDialog } from '../Views';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    text: {
      paddingTop: theme.spacing(2),
    }
  }),
);


interface HeadCreateViewProps {
  project: Backend.ProjectResource, 
  setWorkspace: (head: Backend.Head) => void
};

const createDefaultName = (project: Backend.ProjectResource): string => {
  let result = "dev-"
  let index = 1;
  const heads = Object.keys(project.heads);
  
  while(heads.includes(result + index)) {
    index++;
  }
  return result + index;
}

const HeadCreateView: React.FC<HeadCreateViewProps> = ({project, setWorkspace}) => {
  const classes = useStyles();
  const { service } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);
  const [branchName, setBranchName] = React.useState(createDefaultName(project));
  const [branchFrom, setBranchFrom] = React.useState("main");

  const heads = Object.keys(project.heads);
  const unique = !heads.includes(branchName);
  const empty = branchName.trim().length === 0;
  const error = !unique || empty;
  const errorText = [];
  if(!unique) {
    errorText.push("Branch name must be unique in the project");
  }
  if(empty) {
    errorText.push("Branch name can't be empty");
  }
  
  const handleClose = () => setOpen(false);
  const handleCreate = () => service.heads.create(branchName, project.heads[branchFrom]).onSuccess(newHead => {
    handleClose();
    setWorkspace(newHead);
  })  

  const button = { icon: (<AddIcon color="primary"/>), tooltip: "Create New Branch" };  
  const dialog = {
    title: (<span>Create new branch for: '<i>{project.project.name}</i>'</span>),
    content: (<React.Fragment>
      <div className={classes.text}>
        <TextField required fullWidth
          error={error}
          id="filled-required"
          label="New Branch Name"
          value={branchName}
          variant="outlined"
          helperText={errorText}
          onChange={({target}) => setBranchName(target.value)}
        />
      </div>
      <div className={classes.text}>
        <FormControl variant="outlined" fullWidth>
          <InputLabel htmlFor="outlined-age-native-simple">Branch From *</InputLabel>
          <Select native
            value={branchFrom}
            onChange={({target}) => setBranchFrom(target.value as string)}
            label="Branch From"
            inputProps={{
              name: 'from',
              id: 'outlined-from-native-simple',
            }}
          >
            {heads.map(head => (<option key={head} value={head}>{head}</option>))}
          </Select>
        </FormControl>
      </div>

    </React.Fragment>),
    actions: (<Button onClick={handleCreate} color="primary">Create</Button>)
  }
  
  return (<IconButtonDialog disabled={false} button={button} dialog={dialog} state={{open, setOpen}}/>)
}

export default HeadCreateView;


