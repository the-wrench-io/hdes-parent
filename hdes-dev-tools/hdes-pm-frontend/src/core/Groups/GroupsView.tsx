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
import TabUnselectedOutlinedIcon from '@material-ui/icons/TabUnselectedOutlined';
import DeleteOutlineIcon from '@material-ui/icons/DeleteOutline';

import { Title, Summary, DateFormat } from '.././Views';
import { Resources, Backend } from '.././Resources';



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


interface GroupsViewProps {
  top?: number,
  seeMore?: () => void,
  onDelete: (user: Backend.GroupResource) => void,
  onSelect: (props: {builder: Backend.GroupBuilder, edit?: boolean, activeStep?: number}) => void
  
};

const GroupsView: React.FC<GroupsViewProps> = ({top, seeMore, onSelect, onDelete}) => {
  const { session, service } = React.useContext(Resources.Context);
  const { groups } = session.data;

  
  const classes = useStyles();
  
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [openPopoverRow, setOpenPopoverRow] = React.useState<Backend.GroupResource>();
  const handlePopoverOpen = (event: any, row: Backend.GroupResource) => {
    setOpenPopoverRow(row);
    setAnchorEl(event.currentTarget);
  }
  const handlePopoverClose = () => {
    setAnchorEl(null)
  };
  const openPopover = Boolean(anchorEl);
  
  return (
    <React.Fragment>
      {top ? <Title>Recent Groups</Title> : null}
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell></TableCell>
            <TableCell>Projects</TableCell>
            <TableCell>Users</TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {groups.map((row) => (
            <TableRow key={row.group.id}>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  {row.group.name}
                </Typography>
              </TableCell>
              <TableCell>
                <Typography aria-haspopup="true"
                  aria-owns={openPopover ? 'mouse-over-popover' : undefined}
                  onMouseEnter={(event) => handlePopoverOpen(event, row)} onMouseLeave={handlePopoverClose} >
                  <VisibilityOutlinedIcon fontSize='small'/>
                </Typography>
              </TableCell>
              <TableCell>{Object.keys(row.projects).length}</TableCell>
              <TableCell>{Object.keys(row.users).length}</TableCell>
              <TableCell><DateFormat>{row.group.created}</DateFormat></TableCell>
              <TableCell align="right">
                <IconButton size="small" onClick={() => onSelect({builder: service.groups.builder(row)})} color="inherit">
                  <TabUnselectedOutlinedIcon />
                </IconButton>
                <IconButton size="small" onClick={() => onSelect({ builder: service.groups.builder(row), edit: true })} color="inherit">
                  <EditOutlinedIcon/>
                </IconButton>
                <IconButton size="small" onClick={() => onDelete(row)} color="inherit">
                  <DeleteOutlineIcon/>
                </IconButton>
              </TableCell>
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
        {openPopoverRow ? <Summary resource={openPopoverRow}/> : null}
      </Popover>
      { seeMore ? 
        (<div className={classes.seeMore}>
          <Link color="primary" href="#" onClick={(event: MouseEvent) => {
            event.preventDefault();
            seeMore();
          }}>
            See more groups
          </Link>
        </div>) :
        null
      }
    </React.Fragment>
  );
}

export default GroupsView;

