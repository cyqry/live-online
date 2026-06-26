import { makeStyles } from '@material-ui/styles';
import { Share, ShareOutlined } from '@mui/icons-material';
import { Avatar, Button, Card, CardContent, CardHeader, Typography } from '@mui/material';
import React from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';

const useStyles = makeStyles((theme) => ({
    card: {
        maxWidth: 400,
        margin: '0 auto',
    },
    cardHeader: {
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.common.white,
    },
    avatar: {
        backgroundColor: theme.palette.secondary.main,
    },
    shareButton: {
        marginLeft: 'auto',
    },
}));

const url = "https://ytycc.com";

const ShareCard = () => {
    const classes = useStyles();
    const [copy, setCopy] = useState()
    const handleCopy = () => {
        navigator.clipboard.writeText(url).then(
            (suc) => {
                setCopy(true)
                setTimeout(() => {
                    setCopy(false)
                }, 3000)
            }
        )
    };

    return (
        <Card className={classes.card}>
            <CardHeader
                className={classes.cardHeader}
                avatar={
                    <Avatar className={classes.avatar}>
                        <Share />
                    </Avatar>
                }
                title="平台分享"
            />
            <CardContent>
                <Typography variant="body2" color="textSecondary" component="p">
                    首页链接: <span>{url}</span>
                </Typography>
            </CardContent>
            <Button variant="outlined" color="primary" disabled={copy} className={classes.shareButton} onClick={handleCopy}>
                复制链接
            </Button>
        </Card>
    );
};

export default ShareCard;