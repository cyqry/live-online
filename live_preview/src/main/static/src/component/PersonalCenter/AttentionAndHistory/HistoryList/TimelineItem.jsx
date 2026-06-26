import React from 'react';
import { Grid, Typography } from '@mui/material';
import { makeStyles } from '@material-ui/styles';


const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        alignItems: 'center',
    },
    circle: {
        width: 16,
        height: 16,
        borderRadius: '50%',
        backgroundColor: theme.palette.primary.main,
    },
    line: {
        height: 2,
        flexGrow: 1,
        backgroundColor: theme.palette.grey[400],
        marginLeft: theme.spacing(2),
    },
    date: {
        marginLeft: theme.spacing(2),
    },
}));

const TimelineItem = ({ date }) => {
    const classes = useStyles();

    return (
        <Grid container className={classes.root}>
            <Grid item>
                <div className={classes.circle} />
            </Grid>
            <Grid item>
                <Typography variant="subtitle1" className={classes.date}>
                    {date}
                </Typography>
            </Grid>
            <Grid item className={classes.line} />

        </Grid>
    );
};

export default TimelineItem;
