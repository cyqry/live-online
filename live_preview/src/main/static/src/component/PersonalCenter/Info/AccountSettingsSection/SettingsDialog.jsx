import { makeStyles } from "@material-ui/styles";
import { Cancel, CheckCircle, CreditCardOutlined, EmailOutlined, Person, Refresh, VerifiedUser, Visibility, VisibilityOff } from "@mui/icons-material";
import { Button, Container, Grid, IconButton, InputAdornment, Stack, TextField, Typography } from "@mui/material"
import { motion } from 'framer-motion';
import { useState } from "react";
import { useNotification } from "../../../NotificationProvider";
import { createObjectForForm, filterBlank, getDataFromErrorOrDefault, log } from "../../../../Common/util";
import { identity, updateUser } from "../../../../Api/userApi";

const useStyles = makeStyles((theme) => ({
    changePassword: {
        minHeight: '30vh',
    },
    passwordVisibilityButton: {
        position: 'absolute',
        top: '50%',
        right: '20px',
        transform: 'translateY(-50%)',
    },


    changePhone: {
        fontFamily: 'Roboto, sans-serif',
        height: '25vh',
    },
    inputTel: {
        textAlign: 'center',
    },
    inputText: {
        textAlign: 'center',
    },
    submitButton: {
        backgroundColor: '#1976d2',
        color: 'white',
        '&:hover': {
            backgroundColor: '#1565c0',
        },
    },
    sendButton: {
        padding: '5px',
        backgroundColor: '#1976d2',
        color: 'white',
        '&:hover': {
            backgroundColor: '#1565c0',
        },
    },



    changeIdentity: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        backgroundColor: theme.palette.background.default,
    },
    identityForm: {
        width: '100%',
        marginTop: theme.spacing(1),
    },
    identityTextField: {
        marginTop: theme.spacing(2),
    },
    identityButtonContainer: {
        marginTop: theme.spacing(2),
    },
    identityButton: {
        margin: theme.spacing(0, 0, 2,),
    },



    changeEmailContainer: {
        maxWidth: 340,
        padding: theme.spacing(3),
        paddingTop: 0,
        borderRadius: theme.spacing(1),
    },
    changeEmailTextField: {
        marginBottom: theme.spacing(2),
    },
    changeEmailButton: {
        marginRight: theme.spacing(1),
    },
}));

const EditPassword = ({ closeEdit, change }) => {

    const classes = useStyles();
    const [password, setPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmNewPassword, setConfirmNewPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const { show } = useNotification()

    const handlePasswordChange = (event) => {
        setPassword(event.target.value);
    };

    const handleNewPasswordChange = (event) => {
        setNewPassword(event.target.value);
    };

    const handleConfirmNewPasswordChange = (event) => {
        setConfirmNewPassword(event.target.value);
    };

    const handleCancel = () => {
        closeEdit()
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        let data = createObjectForForm(event.target);
        log(data)
        updateUser(data).then(
            res => {
                show(res.data, "success", 200)
                closeEdit()
                // change()
            },
            e => {
                show(getDataFromErrorOrDefault(e), "error", 2500)
            }
        )

    };

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.5 }}
            className={classes.changePassword}
        >
            <Container maxWidth="xs">
                <form onSubmit={handleSubmit}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField
                                label="当前密码"
                                variant="outlined"
                                fullWidth
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                name="oldPassword"
                                onChange={handlePasswordChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="新密码"
                                variant="outlined"
                                fullWidth
                                type={showPassword ? 'text' : 'password'}
                                value={newPassword}
                                name="password"
                                onChange={handleNewPasswordChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="确认新密码"
                                variant="outlined"
                                fullWidth
                                type={showPassword ? 'text' : 'password'}
                                value={confirmNewPassword}
                                onChange={handleConfirmNewPasswordChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Stack direction={"row"} justifyContent={"center"} spacing={10}>
                                <Button
                                    color="secondary"
                                    onClick={handleCancel}
                                >
                                    取消
                                </Button>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    color="primary"
                                    disabled={!password || !newPassword || !confirmNewPassword || newPassword !== confirmNewPassword}
                                >
                                    确认修改
                                </Button>
                            </Stack>
                        </Grid>
                    </Grid>
                </form>
            </Container>
        </motion.div>
    );
};


const EditEmail = ({ closeEdit, change }) => {
    const classes = useStyles();

    const { showNotification: show } = useNotification();
    const [email, setEmail] = useState('');
    const [verificationCode, setVerificationCode] = useState('');

    const handleChangeEmail = (e) => {
        setEmail(e.target.value);
    };

    const handleChangeVerificationCode = (e) => {
        setVerificationCode(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault()
        let data = createObjectForForm(e.target)
        data = filterBlank(data);
        if (data == {}) {
            show("邮箱和验证码不能为空！", "warning", 3000)
            return
        }
        updateUser(data).then(
            (res) => {
                show('修改成功!', 'success', 2000)
                closeEdit();
                // change()
            },
            (e) => {
                show(getDataFromErrorOrDefault(e), "error", 2500)
            }
        )
            .catch((e) => {
                show(getDataFromErrorOrDefault(e), "error", 2500)
            })


    };

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: { opacity: 1 },
    };

    return (
        <motion.div
            className={classes.changeEmailContainer}
            initial="hidden"
            animate="visible"
            exit="hidden"
            variants={containerVariants}
        >
            <form onSubmit={handleSubmit}>
                <TextField
                    label="新邮箱地址"
                    fullWidth
                    variant="outlined"
                    value={email}
                    name="email"
                    onChange={handleChangeEmail}
                    className={classes.changeEmailTextField}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <EmailOutlined />
                            </InputAdornment>
                        ),
                    }}
                />
                <TextField
                    label="验证码"
                    fullWidth
                    variant="outlined"
                    value={verificationCode}
                    onChange={handleChangeVerificationCode}
                    className={classes.changeEmailTextField}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <VerifiedUser />
                            </InputAdornment>
                        ),
                    }}
                />
                <Grid container justifyContent="center" className={classes.buttonContainer}>
                    <Button
                        variant="outlined"
                        color="primary"
                        className={classes.changeEmailButton}
                        sx={{ mr: "65px" }}
                        onClick={closeEdit}
                    >
                        取消
                    </Button>
                    <Button
                        type="submit"
                        sx={{ ml: "65px" }}
                        variant="contained"
                        color="primary"
                        className={classes.changeEmailButton}
                    >
                        修改
                    </Button>
                </Grid>
            </form>
        </motion.div>
    );
}


const EditPhone = ({ closeEdit, change }) => {
    const classes = useStyles();

    let { show } = useNotification()
    const handleSubmit = (event) => {
        event.preventDefault();
        // 提交表单后的处理逻辑
        let data = createObjectForForm(event.target);
        data = filterBlank(data)
        if (data == {}) {
            show("手机号和验证码不能为空!")
            return;
        }
        updateUser(data).then(
            (res) => {
                show("修改成功！", "success", 2500)
                // change()
                closeEdit()
            },
            (e) => {
                show(getDataFromErrorOrDefault(e), "error")
            }
        )

    };
    const handleCancel = () => {
        // 取消按钮的处理逻辑
        closeEdit()
    };

    const handleGetCode = () => {
        // 获取验证码按钮的处理逻辑

    };
    return (
        <motion.div
            initial={{ opacity: 0, y: -100 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 100 }}
            transition={{ duration: 0.5 }}
            className={classes.changePhone}
        >
            <Container maxWidth="sm">
                <form onSubmit={handleSubmit}>
                    <Stack spacing={2}>
                        <TextField
                            label="新手机号"
                            type="tel"
                            required
                            fullWidth
                            variant="outlined"
                            inputProps={{ maxLength: 20 }}
                            name="phone"
                            InputProps={{
                                classes: { input: classes.inputTel },
                            }}
                        />
                        <TextField
                            label="验证码"
                            type="text"
                            required
                            fullWidth
                            variant="outlined"
                            inputProps={{ maxLength: 8 }}
                            InputProps={{
                                classes: { input: classes.inputText },
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <Button variant="contained" color="primary" size="small" onClick={handleGetCode}>
                                            获取
                                        </Button>
                                    </InputAdornment>
                                ),
                            }}


                        />
                        <Grid container spacing={2}>
                            <Grid item xs={5.5}>
                                <Button type="button" variant="outlined" fullWidth onClick={handleCancel}>
                                    取消
                                </Button>
                            </Grid>
                            <Grid item xs={5.5}>
                                <Button type="submit" variant="contained" fullWidth className={classes.submitButton}>
                                    提交
                                </Button>
                            </Grid>
                        </Grid>
                    </Stack>
                </form>
            </Container>
        </motion.div>
    );
};





const EditIdentity = ({ closeEdit, change }) => {
    const classes = useStyles();
    const [idNumber, setIdNumber] = useState('');
    const [realName, setRealName] = useState('');

    let { show } = useNotification()

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!idNumber || idNumber == '') {
            show("请填写身份证号", "warning", 2300)
            return
        }
        if (!realName || realName == '') {
            show("请填写真实姓名", "warning", 2300)
            return
        }
        // 这里添加处理表单提交的逻辑
        identity(idNumber, realName).then(
            () => {
                change()
                closeEdit()
            },
            (e) => {
                show(getDataFromErrorOrDefault(e), "error", 2200)
            }
        ).catch((e) => {
            show(getDataFromErrorOrDefault(e), "error", 2700)
        })

    };

    const handleCancel = () => {
        closeEdit();
    };

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <Container component="main" maxWidth="xs" className={classes.container}>
                <form className={classes.identityForm} onSubmit={handleSubmit}>
                    <TextField
                        className={classes.identityTextField}
                        variant="outlined"
                        fullWidth
                        id="idNumber"
                        label="身份证号"
                        name="idNumber"
                        value={idNumber}
                        onChange={(e) => setIdNumber(e.target.value)}
                        InputProps={{
                            startAdornment: <CreditCardOutlined sx={{ marginRight: "0.5em" }} />,
                        }}
                    />
                    <TextField
                        className={classes.identityTextField}
                        variant="outlined"
                        fullWidth
                        id="realName"
                        label="真实姓名"
                        name="realName"
                        value={realName}
                        onChange={(e) => setRealName(e.target.value)}
                        InputProps={{
                            startAdornment: <Person sx={{ marginRight: "0.5em" }} />,
                        }}
                    />
                    <Grid container spacing={2} className={classes.identityButtonContainer}>
                        <Grid item xs={6}>
                            <Button
                                variant="outlined"
                                color="secondary"
                                fullWidth
                                className={classes.identityButton}
                                onClick={handleCancel}
                                startIcon={<Cancel />}
                            >
                                取消
                            </Button>
                        </Grid>
                        <Grid item xs={6}>
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                fullWidth
                                className={classes.identityButton}
                                startIcon={<CheckCircle />}
                            >
                                认证
                            </Button>
                        </Grid>
                    </Grid>
                </form>
            </Container>
        </motion.div>
    );
};

export {
    EditIdentity, EditPhone, EditPassword, EditEmail
}