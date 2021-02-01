import React from 'react';

import { makeStyles } from '@material-ui/core/styles';
import Popover from '@material-ui/core/Popover';
import Typography from '@material-ui/core/Typography';
import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import IconButton from '@material-ui/core/IconButton';
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import TabUnselectedOutlinedIcon from '@material-ui/icons/TabUnselectedOutlined';

import { Summary, Title, DateFormat } from '.././Views';
import { Resources, Backend, Mapper } from '.././Resources';

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

type View = {
  id: string;
  name: string;
  created: Date | number[];
  type: string;
  src: Backend.AnyResource;
  onClick: (edit?: boolean) => void;
}

type SearchResults = {
  projects: Backend.ProjectResource[],
  groups: Backend.GroupResource[],
  users: Backend.UserResource[]
}

interface SearchViewProps {
  onSelect: (props: {builder: Backend.AnyBuilder, edit?: boolean, activeStep?: number}) => void
};

const SearchView: React.FC<SearchViewProps> = ({ onSelect }) => {
  const { service, session } = React.useContext(Resources.Context);
  const { users, projects, groups } = session.data;
  const [searchResults, setSearchResults] = React.useState<SearchResults>({users: [], projects: [], groups: []});

  React.useEffect(() => {
    const keyword = session.search;
    if(keyword) {
      setSearchResults({
        projects: projects.filter(u => u.project.name.indexOf(keyword) > -1),
        groups: groups.filter(u => u.group.name.indexOf(keyword) > -1),
        users: users.filter(u => u.user.name.indexOf(keyword) > -1 || (u.user.externalId && u.user.externalId.indexOf(keyword) > -1)),
      })
    }
  }, [session, users, groups, projects])
  
  const toView = (resource: Backend.AnyResource) => new Mapper.Resource<View>(resource)
    .project(resource => ({
      id: resource.project.id,
      name: resource.project.name,
      created: resource.project.created,
      src: resource,
      type: "projects",
      onClick: (edit?) => onSelect({builder: service.projects.builder(resource), edit})
    }))
    .group(resource => ({
      id: resource.group.id,
      name: resource.group.name,
      created: resource.group.created,
      src: resource,
      type: "groups",
      onClick: (edit?) => onSelect({builder: service.groups.builder(resource), edit})
    }))
    .user(resource => ({
      id: resource.user.id,
      name: resource.user.name + ' / ' + resource.user.status,
      created: resource.user.created,
      src: resource,
      type: "users",
      onClick: (edit?) => onSelect({builder: service.users.builder(resource), edit})
    })).map();

  const classes = useStyles();
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [openPopoverRow, setOpenPopoverRow] = React.useState<Backend.AnyResource>();
  const handlePopoverOpen = (event: any, row: Backend.AnyResource) => {
    setOpenPopoverRow(row);
    setAnchorEl(event.currentTarget);
  }
  const handlePopoverClose = () => {
    setAnchorEl(null)
  };
  const openPopover = Boolean(anchorEl);

  const createTable = (items: Backend.AnyResource[]) => items.map(toView).map((row) => (
      <TableRow key={row.id}>
        <TableCell>
          <Typography aria-haspopup="true"
            aria-owns={openPopover ? 'mouse-over-popover' : undefined}
            onMouseEnter={(event) => handlePopoverOpen(event, row.src)} onMouseLeave={handlePopoverClose} >
            {row.name}
          </Typography>
        </TableCell>
        <TableCell>
          <Typography aria-haspopup="true"
            aria-owns={openPopover ? 'mouse-over-popover' : undefined}
            onMouseEnter={(event) => handlePopoverOpen(event, row.src)} onMouseLeave={handlePopoverClose} >
            <VisibilityOutlinedIcon fontSize='small'/>
          </Typography>
        </TableCell>
        <TableCell><DateFormat>{row.created}</DateFormat></TableCell>
        <TableCell>
          <IconButton about="open in tab" size="small" onClick={() => row.onClick()} color="inherit"><TabUnselectedOutlinedIcon/></IconButton>
          <IconButton about="edit in tab" size="small" onClick={() => row.onClick(true)} color="inherit"><EditOutlinedIcon/></IconButton>
        </TableCell>
      </TableRow>
    ));
  
  return (
    <React.Fragment>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell></TableCell>
            <TableCell>Created</TableCell>
            <TableCell></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow>
            <TableCell colSpan={4}><Title disabled={searchResults.projects.length === 0}>Found {searchResults.projects.length} Project(s)</Title></TableCell>
          </TableRow>
          {createTable(searchResults.projects)}
          <TableRow>
            <TableCell colSpan={4}><Title disabled={searchResults.groups.length === 0}>Found {searchResults.groups.length} Group(s)</Title></TableCell>
          </TableRow>
          {createTable(searchResults.groups)}
          <TableRow>
            <TableCell colSpan={4}><Title disabled={searchResults.users.length === 0}>Found {searchResults.users.length} User(s)</Title></TableCell>
          </TableRow>
          {createTable(searchResults.users)}
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
    </React.Fragment>
  );
}

export default SearchView;

