import React, { useState } from 'react';
import { Box, Typography, IconButton, Grid, Dialog, DialogTitle, DialogContent, Badge, Tooltip } from '@mui/material';
import { useStyles } from './styles';
import SettingsIcon from '@mui/icons-material/Settings';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import FaceIcon from '@mui/icons-material/Face';
import VerifiedUser from '@mui/icons-material/VerifiedUser';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import CheckIcon from '@mui/icons-material/Check';
import { EditEmail, EditIdentity, EditPassword, EditPhone } from './SettingsDialog';
import { EmailOutlined } from '@mui/icons-material';

const BindPhoneCheckIcon = ({ isBindPhone }) => {
    const classes = useStyles();
    return (
        <Box position="relative">
            <PhoneIphoneIcon />
            {isBindPhone &&
                <Box className={classes.checkIcon}>
                    <CheckIcon fontSize="small" />
                </Box>}
        </Box>
    )
}
const BindEmailCheckIcon = ({ isBindEmail }) => {
    const classes = useStyles();
    return (
        <Box position="relative">
            <EmailOutlined />
            {isBindEmail &&
                <Box className={classes.checkIcon}>
                    <CheckIcon fontSize="small" />
                </Box>}
        </Box>
    )
}





const VerifiedUserIcon = ({ isAuthentication }) => {
    return (
        <VerifiedUser color={isAuthentication ? 'success' : 'default'} />
    )
}


const settingsFactory = (isBindPhone, isAuthentication, isBindEmail, setEditOpen, change) => {
    return [
        {
            title: '密码设置',
            description: '修改登录密码',
            icon: <VpnKeyIcon />,
            dialogWholeStyle: {
                backgroundColor: '#f0f2f5',
            },
            handleClick: () => {
                setEditOpen('密码设置', true);
            },
            dialog: <EditPassword closeEdit={() => { setEditOpen('密码设置', false); }} change={change} />
        },
        {
            title: '身份认证',
            description: '进行身份认证',
            icon: <VerifiedUserIcon isAuthentication={isAuthentication} />,
            handleClick: () => {
                setEditOpen('身份认证', true);
            },
            dialogWholeStyle: { backgroundColor: '#f0f2f5' },
            dialog: <EditIdentity closeEdit={() => { setEditOpen('身份认证', false); }} change={change} />
        },
        {
            title: '修改绑定手机',
            description: '更新您的绑定手机号码',
            icon: (
                <BindPhoneCheckIcon isBindPhone={isBindPhone} />
            ),
            dialogWholeStyle: { backgroundColor: '#f0f2f5' },
            handleClick: () => {
                setEditOpen('修改绑定手机', true);
            },
            dialog: <EditPhone closeEdit={() => { setEditOpen('修改绑定手机', false); }} change={change} />
        },
        {
            title: '修改邮箱',
            description: '更新您的绑定邮箱',
            icon: (
                <BindEmailCheckIcon isBindEmail={isBindEmail} />
            ),
            handleClick: () => {
                setEditOpen('修改邮箱', true);
            },
            dialogWholeStyle: { backgroundColor: '#f0f2f5' },
            dialog: <EditEmail closeEdit={() => { setEditOpen('修改邮箱', false); }} change={change} />

        }
        // 添加其他设置项时，添加这里，并在下面添加一个状态为false
    ];
}



const AccountSettingsSection = ({ isBindPhone, isAuthentication, isBindEmail, change }) => {
    const classes = useStyles();
    const [openDialog, setOpenDialog] = useState({
        '密码设置': false,
        '个性ID修改': false,
        '身份认证': false,
        '修改绑定手机': false,
        '修改邮箱': false,
    });

    const setEditOpen = (key, flag) => {
        setOpenDialog((openDialog) => { return { ...openDialog, [key]: flag } });
    };

    let settings = settingsFactory(isBindPhone, isAuthentication, isBindEmail, setEditOpen, change);
    return (
        <Box className={classes.root}>
            <Typography variant="h6" className={classes.title}>
                账号设置
            </Typography>
            <Grid container spacing={2} className={classes.settings}>
                {settings.map((setting, index) => {
                    return (
                        <Grid item xs={12} sm={6} key={index}>
                            <Box className={classes.settingItem}>
                                <Box className={classes.icon}>{setting.icon}</Box>
                                <Box className={classes.settingInfo}>
                                    <Typography variant="subtitle1" className={classes.settingTitle}>
                                        {setting.title}
                                    </Typography>
                                    <Typography variant="body2" className={classes.settingDescription}>
                                        {setting.description}
                                    </Typography>
                                </Box>
                                <IconButton
                                    onClick={(setting.title != '身份认证' || !isAuthentication) ? setting.handleClick : undefined}
                                    size="small"
                                    className={classes.settingButton}
                                >
                                    {setting.title == '身份认证' && isAuthentication ? <VerifiedBadge /> : < SettingsIcon />}
                                </IconButton>
                            </Box>
                        </Grid>
                    )
                })}
            </Grid>
            {/* 好用的 */}
            {
                settings.map((setting) => (
                    <Dialog
                        open={openDialog[setting.title]}
                        onClose={() => setEditOpen(setting.title, false)}
                        aria-labelledby={`${setting.title}-dialog-title`}
                        key={`${setting.title}-dialog`}
                        PaperProps={{ style: setting.dialogWholeStyle }}
                    >
                        <DialogTitle className={classes.dialogTitle} id={`${setting.title}-dialog-title`}>{setting.title}</DialogTitle>
                        <DialogContent style={{ paddingTop: "1vh" }}>
                            {setting.dialog}
                        </DialogContent>
                    </Dialog>
                ))
            }
        </Box >

    );
};


const VerifiedBadge = () => {
    const classes = useStyles();

    return (
        <Box className={classes.verifiedContainer}>
            <Typography variant="body1" className={classes.verifiedText}>
                已认证
            </Typography>
        </Box>
    );
};


export default AccountSettingsSection;
