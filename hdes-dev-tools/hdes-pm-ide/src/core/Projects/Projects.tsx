import React, { MouseEvent } from 'react';
import moment from 'moment';


import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Title from './Title';

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

function preventDefault(event: MouseEvent) {
  event.preventDefault();
}

const useStyles = makeStyles((theme) => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
}));

export default function Projects() {
  const classes = useStyles();
  return (
    <React.Fragment>
      <Title>Recent Projects</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Users</TableCell>
            <TableCell>Modified last</TableCell>
            <TableCell>Created</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.id}>
              <TableCell>{row.name}</TableCell>
              <TableCell>{row.users}</TableCell>
              <TableCell>{row.modifiedDaysAgo} day(s) ago</TableCell>
              <TableCell>{row.created}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <div className={classes.seeMore}>
        <Link color="primary" href="#" onClick={preventDefault}>
          See more projects
        </Link>
      </div>
    </React.Fragment>
  );
}