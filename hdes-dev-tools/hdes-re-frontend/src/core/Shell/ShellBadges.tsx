import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import Badge from '@material-ui/core/Badge';

import ClickAwayListener from '@material-ui/core/ClickAwayListener';
import Grow from '@material-ui/core/Grow';
import Paper from '@material-ui/core/Paper';
import Popper from '@material-ui/core/Popper';


const useStyles = makeStyles((theme) => ({
  root: {
    color: theme.palette.primary.main,
    marginRight: 100,
  },
  
}));


interface ShellBadgeProps {
  open: boolean;
  badgeRef: HTMLElement|null,
  handleClose: () => void;
  children: BadgeValue;
}

const ShellBadge: React.FC<ShellBadgeProps> = ({open, handleClose, children, badgeRef}) => {
  return (<Popper open={open} anchorEl={badgeRef} role={undefined} transition disablePortal>
  {({ TransitionProps, placement }) => (
    <Grow
      {...TransitionProps}
      style={{ transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom' }}
    >
      <Paper>
        <ClickAwayListener onClickAway={handleClose}>
          <div>
            {children.onClick()}
          </div>
        </ClickAwayListener>
      </Paper>
    </Grow>
  )}
</Popper>);
}


interface BadgeValue {
  icon: React.ReactNode;
  label: string;
  onClick: () => React.ReactNode,
}

interface ShellBadgesProps {
  children: BadgeValue[]
}

const ShellBadges: React.FC<ShellBadgesProps> = (props) => {
  const classes = useStyles();

  const [openBadge, setOpenBadge] = React.useState<number>(-1);
  const [badgeRef, setBadgeRef] = React.useState<HTMLElement|null>(null);  

  const handleBadgeOpen = (event: any, index: number) => {
    setOpenBadge(index);
    setBadgeRef(event.currentTarget);
  };

  const result = props.children.map((b, index) => (
    <IconButton key={index} color="inherit" className={classes.root} onClick={(event) => handleBadgeOpen(event, index)}>
      <Badge badgeContent={b.label} color="secondary">{b.icon}</Badge>
      <ShellBadge badgeRef={badgeRef} open={index === openBadge} handleClose={() => setOpenBadge(-1)}>{b}</ShellBadge>
    </IconButton>
  ))
  return (<>{result}</>);
}

export default ShellBadges;
