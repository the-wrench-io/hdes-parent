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

// Generate Order Data
function createData(id: string, name: string, users: number, modifiedDaysAgo: number, created: Date) {
  return { id, name, users, modifiedDaysAgo, created : moment(created).format(DATE_FORMAT) };
}

const DATE_FORMAT = "MMM Do YY";
 
const rows = [
  createData('0', 'scoring project', 2, 10, new Date("2019-01-16")),
  createData('1', 'casco pricing project', 2, 10, new Date("2018-01-16")),
  createData('2', 'risk project', 2, 10, new Date("2017-01-16")),
  createData('3', 'room condition project', 2, 10, new Date("2015-01-16"))
];


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
            <TableCell>Modified last</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.id}>
              <TableCell>{row.name}</TableCell>
              <TableCell>{row.users}</TableCell>
              <TableCell>{row.modifiedDaysAgo} day(s) ago</TableCell>
              <TableCell>{row.created}</TableCell>
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

