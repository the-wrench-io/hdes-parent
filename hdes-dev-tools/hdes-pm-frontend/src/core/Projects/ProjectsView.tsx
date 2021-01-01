import React, { MouseEvent } from 'react';
import moment from 'moment';

import { makeStyles } from '@material-ui/core/styles';
import Link from '@material-ui/core/Link';
import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import IconButton from '@material-ui/core/IconButton';

import AddUserToProject from './AddUserToProject';
import { Title } from '.././Views';
import { Resources, Backend } from '.././Resources';


const DATE_FORMAT = "MMM Do YY";


const useStyles = makeStyles((theme) => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
}));

interface ProjectsViewProps {
  top?: number,
  seeMore?: () => void
};


const ProjectsView: React.FC<ProjectsViewProps> = ({top, seeMore}) => {
  const classes = useStyles();
  
  const { service } = React.useContext(Resources.Context);
  const [projects, setProjects] = React.useState<Backend.ProjectResource[]>([]);
  React.useEffect(() => service.projects.query({ top }).onSuccess(setProjects), [service.projects, top])
  
  const [open, setOpen] = React.useState(false);
  const openAdd = () => setOpen(true);
  const closeAdd = () => setOpen(false);

  return (
    <React.Fragment>
      <AddUserToProject open={open} handleClose={closeAdd} />
      <Title>{top ? 'Recent Projects' : 'All Projects'}</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Users</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {projects.map((row) => (
            <TableRow key={row.project.id}>
              <TableCell>{row.project.name}</TableCell>
              <TableCell>{Object.keys(row.users).length}</TableCell>
              <TableCell>{moment(row.project.created).format(DATE_FORMAT)}</TableCell>
              <TableCell><IconButton size="small" onClick={openAdd} color="inherit"><EditOutlinedIcon/></IconButton></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
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

