import React from 'react';
import { AppBar, Toolbar, Typography } from '@mui/material';

const Navbar = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6">直播间历史记录</Typography>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
