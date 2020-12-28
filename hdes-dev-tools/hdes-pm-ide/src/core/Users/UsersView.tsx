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

import { Title } from '.././Views';
import { Resources, Backend } from '.././Resources';


const DATE_FORMAT = "MMM Do YY";
 
const useStyles = makeStyles((theme) => ({
  seeMore: {
    marginTop: theme.spacing(3),
  },
}));


interface UserViewProps {
  top?: number,
  seeMore?: () => void
};


const UsersView: React.FC<UserViewProps> = ({top, seeMore}) => {
  const { service } = React.useContext(Resources.Context);
  const [users, setUsers] = React.useState<Backend.User[]>([]);
  React.useEffect(() => service.users.query({ top }).onSuccess(setUsers), [service.users, top])
  
  const classes = useStyles();
  
  const [open, setOpen] = React.useState(false);
  const openAdd = () => setOpen(true);
  const closeAdd = () => setOpen(false);

  return (
    <React.Fragment>
      <Title>{top ? 'Recent Users' : 'All Users'}</Title>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>External Id</TableCell>
            <TableCell>Groups</TableCell>
            <TableCell>Projects</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((row) => (
            <TableRow key={row.id}>
              <TableCell>{row.name}</TableCell>
              <TableCell>{row.externalId}</TableCell>
              <TableCell>{row.groups}</TableCell>
              <TableCell>{row.projects}</TableCell>
              <TableCell>{moment(row.created).format(DATE_FORMAT)}</TableCell>
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
            See more users
          </Link>
        </div>) :
        null
      }
    </React.Fragment>
  );
}

export default UsersView;

