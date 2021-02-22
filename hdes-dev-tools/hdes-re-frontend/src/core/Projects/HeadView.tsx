import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import SubdirectoryArrowRightIcon from '@material-ui/icons/SubdirectoryArrowRight';

import { Backend } from '../Resources';
import HeadDeleteView from './HeadDeleteView';
import HeadMergeView from './HeadMergeView';
import HeadDescView from './HeadDescView';



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

interface HeadViewProps {
  project: Backend.ProjectResource,
  head: Backend.Head,
}

const HeadView: React.FC<HeadViewProps> = ({project, head}) => {
  const classes = useStyles();
  const secondaryActions: React.ReactChild[] = [];
  
  if(head.name !== 'main') {
    secondaryActions.push(<HeadDeleteView project={project} head={head}/>);
    secondaryActions.push(<HeadMergeView project={project} head={head}/>); 
  }
  
  return (<>
    <ListItem className={classes.root}>
      <ListItemAvatar>
        <Tooltip title={"Edit This Branch"}>
          <IconButton edge="end" aria-label="open">
            <SubdirectoryArrowRightIcon color="primary" fontSize="small"/>
          </IconButton>
        </Tooltip>
      </ListItemAvatar>
      <ListItemText primary={head.name} secondary={<HeadDescView project={project} head={head} />} />
      <ListItemSecondaryAction>
        { secondaryActions.map((e, index) => <React.Fragment key={index}>{e}</React.Fragment>)}
      </ListItemSecondaryAction>
    </ListItem>
    </>);
}

export default HeadView;
