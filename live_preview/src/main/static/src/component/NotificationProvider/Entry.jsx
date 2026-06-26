import React, { useState, useEffect } from 'react';
import { Alert, IconButton, Slide, Snackbar } from '@mui/material';
import { Close } from '@mui/icons-material';
import { makeStyles } from '@material-ui/styles';

const useStyles = makeStyles(theme => ({
    alert: {
        borderRadius: '8px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        animation: '$appear 500ms',
    },
    '@keyframes appear': {
        '0%': {
            opacity: 0,
            transform: 'translateY(-20px)',
        },
        '100%': {
            opacity: 1,
            transform: 'translateY(0)',
        },
    },
}));

const Entry = ({ message, status, duration, style, barKey }) => {
    const [open, setOpen] = useState(false);
    const classes = useStyles();

    useEffect(() => {
        if (message) {
            setOpen(true);
        }
    }, [message,barKey]);

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };


    return (
        <Snackbar
            key={barKey}
            open={open}
            autoHideDuration={duration}
            onClose={handleClose}
            anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            TransitionComponent={Slide}
            TransitionProps={{ direction: 'down' }}
        >
            <Alert
                severity={status}
                variant="filled"
                elevation={0}
                onClose={handleClose}
                action={
                    <IconButton
                        size="small"
                        aria-label="close"
                        color="inherit"
                        onClick={handleClose}
                    >
                        <Close fontSize="small" />
                    </IconButton>
                }
                className={classes.alert}
            >
                {message}
            </Alert>
        </Snackbar>
    );
};

export default Entry;
