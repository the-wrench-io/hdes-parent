import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';
import SubdirectoryArrowRightIcon from '@material-ui/icons/SubdirectoryArrowRight';
import Tooltip from '@material-ui/core/Tooltip';
import DeleteForeverIcon from '@material-ui/icons/DeleteForever';

import { Backend, Resources } from '../Resources';
import HeadDeleteDialog from './HeadDeleteDialog';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      backgroundColor: theme.palette.background.paper,
      paddingLeft: '32px',
      paddingTop: '3px',
      paddingBottom: '3px',
      marginBottom: '0px',
    },
  }),
);

const createHeadDesc = (project: Backend.ProjectResource, head: Backend.Head): React.ReactNode => {
  
  const headState = project.states[head.name];
  if(head.name === 'main') {
    const ahead = Object.values(project.states)
      .filter(s => s.type === 'ahead')
      .map(s => `${s.head} by ${s.commits}`);
    if(ahead.length > 0) {
      return (<span>Main branch is behind of: {ahead.join(", ")} commits</span>);
    }
  } else if(headState.type === 'same') {
    return (<span>Same assets as in main</span>);
  } else if(headState.type === 'behind') {
    return (<span>Assets are behind of main by: {headState.commits} commits</span>);
  } else {
    return (<span>Assets are ahead of main by: {headState.commits} commits</span>);
  }
  return null;
}


interface HeadViewProps {
  project: Backend.ProjectResource,
  head: Backend.Head,
}

const HeadView: React.FC<HeadViewProps> = ({project, head}) => {
  const classes = useStyles();
  const { session } = React.useContext(Resources.Context);
  const [open, setOpen] = React.useState(false);
  
  return (<>
    <HeadDeleteDialog open={open} onClose={() => setOpen(false)} resource={{head, project}} />
    <ListItem className={classes.root}>
      <ListItemAvatar>
        <Tooltip title={"Edit This Branch"}>
          <IconButton edge="end" aria-label="open">
            <SubdirectoryArrowRightIcon color="primary" fontSize="small"/>
          </IconButton>
        </Tooltip>
      </ListItemAvatar>
      <ListItemText primary={head.name} secondary={createHeadDesc(project, head)} />
      <ListItemSecondaryAction>
        { head.name === 'main' ? null : (
          <Tooltip title={"Delete This Branch"}>
            <IconButton edge="end" aria-label="delete branch" onClick={() => setOpen(true)}>
              <DeleteForeverIcon />
            </IconButton>
          </Tooltip>)}
      </ListItemSecondaryAction>
    </ListItem>
    </>);
}

export default HeadView;
