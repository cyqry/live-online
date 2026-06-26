import React from 'react';
import { List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography } from '@mui/material';
import { AccountCircle, CheckCircle, Dashboard, Flag, Group, History, People, Person2, Subscriptions, SupervisorAccount } from '@mui/icons-material';
import { makeStyles } from "@material-ui/styles";
import { NavLink, useLocation } from 'react-router-dom';


const useStyles = makeStyles({
    sidebar: {
        width: 240,
        backgroundColor: '#e8dcdc',
        paddingTop: 20,
        paddingBottom: 20,
        boxShadow: '0 2px 5px rgba(0, 0, 0, 0.1)',
    },
    title: {
        display: "inline-block",
        marginLeft: "21px",
        marginBottom: "10px",
        fontWeight: "bold"
    },
    link: {
        color: "#292124"
    },
    selectionLink: {
        backgroundColor: "#0e0c111c",
        borderLeft: "3px solid #f80",
        color: "#333",
        fontWeight: "bold",
    }
});
const Sidebar = ({ style, user, isAnchor }) => {
    const classes = useStyles();
    const location = useLocation();
    const currentPath = location.pathname;
    return (
        <List className={classes.sidebar} style={style}>
            <Typography className={classes.title} component={"b"} >个人中心</Typography>
            <NavLink to={"/i/info"} className={classes.link} >
                <ListItemButton className={currentPath.includes("/i/info") ? classes.selectionLink : ""}>
                    <ListItemIcon>
                        <AccountCircle />
                    </ListItemIcon>
                    <ListItemText primary="我的信息" />
                </ListItemButton>
            </NavLink>
            <NavLink to={"/i/sub"} className={classes.link}>
                <ListItemButton className={currentPath.includes("/i/sub") ? classes.selectionLink : ""}>
                    <ListItemIcon>
                        <Subscriptions />
                    </ListItemIcon>
                    <ListItemText primary="订阅与历史" />
                </ListItemButton>
            </NavLink>
            <NavLink to={"/i/manager"} className={classes.link}>
                <ListItemButton className={currentPath.includes("/i/manager") ? classes.selectionLink : ""} >
                    <ListItemIcon>
                        <SupervisorAccount />
                    </ListItemIcon>
                    <ListItemText primary="我是房管" />
                </ListItemButton>
            </NavLink>
            <NavLink to={"/i/registry"} className={classes.link}>
                <ListItemButton className={currentPath.includes("/i/registry") ? classes.selectionLink : ""}  >
                    <ListItemIcon>
                        <CheckCircle />
                    </ListItemIcon>
                    <ListItemText primary="主播认证" />
                </ListItemButton>
            </NavLink>
            {isAnchor &&
                <NavLink to={"/i/myRoomAdmin"} className={classes.link}>
                    <ListItemButton className={currentPath.includes("/i/myRoomAdmin") ? classes.selectionLink : ""} >
                        <ListItemIcon>
                            <Person2 />
                        </ListItemIcon>
                        <ListItemText primary="我的房管" />
                    </ListItemButton>
                </NavLink>
            }
            {
                user && user.role >= 2 &&
                <NavLink to={"/i/reportInfo"} className={classes.link}>
                    <ListItemButton className={currentPath.includes("/i/reportInfo") ? classes.selectionLink : ""}>
                        <ListItemIcon>
                            <Flag />
                        </ListItemIcon>
                        <ListItemText primary="房间举报" />
                    </ListItemButton>
                </NavLink>
            }
        </List>
    );
};

export default Sidebar;
