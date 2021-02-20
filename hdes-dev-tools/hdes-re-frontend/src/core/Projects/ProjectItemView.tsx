import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';
import FolderIcon from '@material-ui/icons/Folder';
import SubdirectoryArrowRightIcon from '@material-ui/icons/SubdirectoryArrowRight';
import Tooltip from '@material-ui/core/Tooltip';
import Link from '@material-ui/core/Link';
import Collapse from '@material-ui/core/Collapse';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';

import { Backend } from '../Resources';


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      backgroundColor: theme.palette.background.paper,
      paddingTop: '12px',
      paddingBottom: '12px',
      marginBottom: '12px',
    },
    branch: {
      backgroundColor: theme.palette.background.paper,
      paddingLeft: '32px',
      paddingTop: '3px',
      paddingBottom: '3px',
      marginBottom: '0px',
    },
  }),
);

interface ProjectsItemViewProps {
  project: Backend.ProjectResource
};


const ProjectItemView: React.FC<ProjectsItemViewProps> = ({project}) => {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);
  
  const branches: React.ReactChild[] = [];
  const heads = Object.values(project.heads);
  const headSummary: React.ReactElement[] = [];
  let index = 1;
  for(const head of heads) {
    const linkToHead = (
      <Tooltip key={index + "-link"} title={`Load: ${project.project.name}, branch: ${head.name}`}>
        <Link component="button" variant="body2" onClick={() => { console.info("I'm a button."); }}>
          {head.name}
        </Link>
      </Tooltip>)
    headSummary.push(linkToHead);

    if(open) {
      const branch = (<ListItem className={classes.branch}>
        <ListItemAvatar>
          <SubdirectoryArrowRightIcon color="primary" fontSize="small"/>
        </ListItemAvatar>
        <ListItemText primary={head.name} secondary={<span></span>} />
        <ListItemSecondaryAction>
          <Tooltip title={"Manage branches"}>
            <IconButton edge="end" aria-label="more-or-less">
            </IconButton>
          </Tooltip>
        </ListItemSecondaryAction>
      </ListItem>)
      
      branches.push(branch);    
    } 
    
    if(heads.length > index++) {
      branches.push(<Divider key={index + "-divider"}/>);
      headSummary.push(<span key={index + "-spacer"}>, </span>);
    }
    
  }
  
  return (
    <div className={classes.root}>
      <ListItem>
        <ListItemAvatar>
          <Avatar><FolderIcon/></Avatar>
        </ListItemAvatar>
        <ListItemText primary={project.project.name} secondary={<span>Contains {heads.length} branches: {headSummary}</span>} />
        <ListItemSecondaryAction>
          <Tooltip title={"Manage branches"}>
            <IconButton edge="end" aria-label="more-or-less" onClick={() => setOpen(!open)}>
              {open ? <ExpandLessIcon /> : <ExpandMoreIcon />}
            </IconButton>
          </Tooltip>
        </ListItemSecondaryAction>
      </ListItem>
      
      <Collapse in={open} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          {branches.map((b, index) => <React.Fragment key={index}>{b}</React.Fragment>) }
        </List>
      </Collapse>
    </div>
  );
}

export default ProjectItemView;
