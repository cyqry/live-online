import React, { useState } from 'react';
import { AppBar, Tabs, Tab } from '@mui/material';
import { makeStyles } from '@material-ui/styles';
import { NavLink, useLocation, useMatch } from 'react-router-dom';
import { log } from '../../../../Common/util';

const useStyles = makeStyles((theme) => ({
    appBar: {
        backgroundColor: theme.palette.background.default,
        borderBottom: `1px solid ${theme.palette.divider}`,
    },
    tab: {
        textTransform: 'none',
        fontWeight: theme.typography.fontWeightMedium,
        fontSize: theme.typography.pxToRem(18),
        minWidth: 0,
        minHeight: 48,
        marginRight: theme.spacing(1),
        color: theme.palette.text.primary,
    },
    indicator: {
        backgroundColor: theme.palette.primary.main,
        height: 3,
    },
    selectedTab: {
        color: theme.palette.primary.main,
        fontWeight: theme.typography.fontWeightBold,
    },
}));

const NavigationTabs = () => {
    const classes = useStyles();



    const [value, setValue] = useState(0);

    const isSubscribeActive = useLocation().pathname.endsWith("/subscribe");
    const isHistoryActive = useLocation().pathname.endsWith('/history');

    log(isSubscribeActive, isHistoryActive)
    if (isHistoryActive && value == 0) {
        setValue(1);
    } else if (isSubscribeActive && value == 1) {
        setValue(0);
    }


    const handleChange = (event, newValue) => {
        log(newValue)
        setValue(newValue);
    };

    const activeName = (active) => {
        return active ? classes.selectedTab : '';
    };

    return (
        <AppBar position="static" className={classes.appBar}>
            <Tabs value={value} onChange={handleChange} centered classes={{ indicator: classes.indicator }}>
                <Tab
                    label="订阅"
                    className={`${classes.tab} ${activeName(isSubscribeActive)}`}
                    component={NavLink}
                    to="./subscribe" />
                <Tab
                    label="历史记录"
                    className={`${classes.tab} ${activeName(isHistoryActive)}`}
                    component={NavLink}
                    to="./history" />
            </Tabs>
        </AppBar>
    );
};

export default NavigationTabs;
