import React, { useState, useCallback } from "react";
import {
    Dialog,
    DialogContent,
    DialogTitle,
    Box,
    TextField,
    Button,
    Avatar,
    Input,
    CircularProgress,
    Grid,
} from "@mui/material";
import defalutAvatar from '../../../../static/img/OIP.jpg'
import { PhotoCamera } from "@mui/icons-material";
import { makeStyles } from "@material-ui/styles";
import { filterBlank, getDataFromErrorOrDefault, log } from "../../../../Common/util";
import { updateUser } from '../../../../Api/userApi';
import { useNotification } from "../../../NotificationProvider";
const useStyles = makeStyles((theme) => ({
    root: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
    },
    avatar: {
        width: theme.spacing(15),
        height: theme.spacing(15),
        alignItems: "center",
        justifyContent: "center",
        marginLeft: "14px",
        marginBottom: theme.spacing(2),
    },
    input: {
        display: "none",
    },
    textField: {
        marginBottom: theme.spacing(2),
    },
    button: {
        margin: theme.spacing(2),
    },
    title: {
        color: "#3f51b5",
        marginBottom: theme.spacing(3),
    },
    buttonBox: {
        display: "flex",
        justifyContent: "center",
        marginTop: theme.spacing(2),
    },
}));

export default function EditDialog({ open, setEditOpen, oldUserProfile, change }) {
    const classes = useStyles();

    return (
        <Dialog
            open={open}
            onClose={() => {
                setEditOpen(false);
            }}
        >
            <DialogTitle className={classes.title}>编辑个人信息</DialogTitle>
            <DialogContent style={{ paddingTop: "1ch" }}>
                <EditProfile setEditOpen={setEditOpen} oldUserProfile={oldUserProfile} change={change} />
            </DialogContent>
        </Dialog>
    );
}

const EditProfile = ({ setEditOpen, oldUserProfile, change }) => {
    const classes = useStyles();
    const [nickname, setNickname] = useState(oldUserProfile.nickname == null ? undefined : oldUserProfile.nickname);
    const [personalityId, setPersonalId] = useState(oldUserProfile.personalityId == null ? undefined : oldUserProfile.personalityId);
    const [avatar, setAvatar] = useState();
    const [isSaving, setIsSaving] = useState(false);
    const { show } = useNotification();


    const handleImageChange = useCallback((event) => {
        const file = event.target.files[0];
        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onloadend = () => {
            setAvatar(reader.result);
        };
    }, []);

    const handleSave = () => {
        async function run() {
            setIsSaving(true);
            log(avatar && avatar.replace)
            let data = filterBlank({ nickname, personalityId, avatarBase: avatar && avatar.replace ? avatar.replace(/^data:image\/\w+;base64,/, '') : undefined });
            log("updateData", data)
            try {
                await updateUser(data)
                setEditOpen(false)
                // change() //通知父父父组件重新加载数据
                window.location.reload()
            } catch (e) {
                show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 3000)
            }
            setIsSaving(false);
        }
        run()
    };

    const handleClose = useCallback(() => {
        setEditOpen(false);
    }, [setEditOpen]);

    let avatarSrc = avatar ? avatar : ((oldUserProfile && oldUserProfile.base64Src) ? oldUserProfile.base64Src : defalutAvatar);
    return (
        <Box className={classes.root}>
            <Grid container spacing={4}>
                <Grid item xs={12} sm={3.7} style={{ textAlign: "center" }}>
                    <Avatar className={classes.avatar} src={avatarSrc} />
                    <label htmlFor="avatar-upload">
                        <Input
                            inputProps={{ accept: "image/*", capture: "camera" }}
                            id="avatar-upload"
                            type="file"
                            className={classes.input}
                            onChange={handleImageChange}
                        />
                        <Button
                            variant="contained"
                            color="primary"
                            component="span"
                            startIcon={<PhotoCamera />}
                        >
                            上传头像
                        </Button>
                    </label>
                </Grid>
                <Grid item xs={12} sm={8.3}>
                    <TextField
                        className={classes.textField}
                        label="昵称"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        fullWidth
                    />
                    <TextField
                        className={classes.textField}
                        label="个性ID"
                        value={personalityId}
                        onChange={(e) => setPersonalId(e.target.value)}
                        fullWidth
                    />
                </Grid>
            </Grid>
            <Box className={classes.buttonBox}>
                <Button onClick={handleClose} color="primary">
                    取消
                </Button>
                <Button
                    className={classes.button}
                    variant="contained"
                    color="primary"
                    onClick={handleSave}
                    disabled={isSaving}
                >
                    {isSaving && (
                        <CircularProgress
                            size={12}
                        />
                    )}
                    <span>保存</span>
                </Button>
            </Box>
        </Box>
    );
};