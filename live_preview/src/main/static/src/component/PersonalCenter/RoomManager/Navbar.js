// Navbar.js
import React from 'react';
import AppBar from '@mui/material/AppBar';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import { makeStyles } from '@material-ui/styles';

const useStyles = makeStyles((theme) => {
    return {
        appBar: {
            position: "static"
        },
        tabs:{
            backgroundColor:"white"
        }
    }
})

const Navbar = ({ activeTab, handleChange }) => {
    const classes = useStyles();
    return (
        <AppBar className={classes.appBar} >
            <Tabs className={classes.tabs} value={activeTab} onChange={handleChange}>
                <Tab label="任职列表" />
                <Tab label="禁言记录" />
                <Tab label="操作记录" />
            </Tabs>
        </AppBar>
    );
};

export default Navbar;
