import React, { MouseEvent } from 'react';

import { makeStyles } from '@material-ui/core/styles';
import Popover from '@material-ui/core/Popover';
import Typography from '@material-ui/core/Typography';
import Link from '@material-ui/core/Link';
import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import IconButton from '@material-ui/core/IconButton';
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';

import { Title, DateFormat } from '.././Views';
import { Resources, Backend } from '.././Resources';
import ProjectSummary from './ProjectSummary';

 
const useStyles = makeStyles((theme) => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
  popover: {
    pointerEvents: 'none',
    
  },
  paper: {
    //backgroundColor: 'transparent',
    //padding: theme.spacing(1),
  },
}));


interface ProjectsViewProps {
  top?: number,
  seeMore?: () => void,
  onEdit: (project: Backend.ProjectBuilder) => void
  
};

const ProjectsView: React.FC<ProjectsViewProps> = ({top, seeMore, onEdit}) => {
  const { service } = React.useContext(Resources.Context);
  const [projects, setProjects] = React.useState<Backend.ProjectResource[]>([]);
  React.useEffect(() => service.projects.query({ top }).onSuccess(setProjects), [service.projects, top])
  
  const classes = useStyles();
  
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [openPopoverRow, setOpenPopoverRow] = React.useState<Backend.ProjectResource>();
  const handlePopoverOpen = (event: any, row: Backend.ProjectResource) => {
    setOpenPopoverRow(row);
    setAnchorEl(event.currentTarget);
  }
  const handlePopoverClose = () => {
    setAnchorEl(null)
  };
  const openPopover = Boolean(anchorEl);
  
  return (
    <React.Fragment>
      <Title>{top ? 'Recent Projects' : 'All Projects'}</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell></TableCell>
            <TableCell>Groups</TableCell>
            <TableCell>Users</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {projects.map((row) => (
            <TableRow key={row.project.id}>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  {row.project.name}
                </Typography>
              </TableCell>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  <VisibilityOutlinedIcon fontSize='small'/>
                </Typography>
              </TableCell>
              <TableCell>{Object.keys(row.groups).length}</TableCell>
              <TableCell>{Object.keys(row.users).length}</TableCell>
              <TableCell><DateFormat>{row.project.created}</DateFormat></TableCell>
              <TableCell><IconButton size="small" onClick={() => onEdit(service.projects.builder(row))} color="inherit"><EditOutlinedIcon/></IconButton></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Popover id="mouse-over-popover"
          className={classes.popover}
          classes={{paper: classes.paper }}
          open={openPopover}
          anchorEl={anchorEl}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
          transformOrigin={{ vertical: 'top', horizontal: 'left' }}
          onClose={handlePopoverClose}
          disableRestoreFocus>
        {openPopoverRow ? <ProjectSummary resource={openPopoverRow}/> : null}
      </Popover>
      { seeMore ? 
        (<div className={classes.seeMore}>
          <Link color="primary" href="#" onClick={(event: MouseEvent) => {
            event.preventDefault();
            seeMore();
          }}>
            See more projects
          </Link>
        </div>) :
        null
      }
    </React.Fragment>
  );
}

export default ProjectsView;

