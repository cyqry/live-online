import React, { useState } from 'react';
import { makeStyles } from "@material-ui/styles";
import { LockOutlined, Visibility, VisibilityOff } from "@mui/icons-material";
import { Avatar, Box, Button, Checkbox, Container, CssBaseline, FormControl, FormControlLabel, Grid, IconButton, InputAdornment, InputLabel, MenuItem, OutlinedInput, Select, TextField, Typography } from "@mui/material";
import { Link } from "react-router-dom";
import { useRef } from 'react';
import { signUp } from '../../Api/userApi';
import { useNotification } from '../NotificationProvider';
import { Ok } from './Ok'; import { fieldMap } from '../../Common/const';
import { getDataFromErrorOrDefault, log } from '../../Common/util';
;

function Copyright() {
    return (
        <Typography variant="body2" color="textSecondary" align="center">
            {'版权所有 © '}
            <Link color="inherit" href="https:/ytycc.com/">
                ytycc.com
            </Link>{' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}

const useStyles = makeStyles((theme) => ({
    main: {
        width: "100%",
        textAlign: "center",
    },
    container: {
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        margin: "0px auto",
        width: "40%",
        minHeight: '100vh',
        paddingTop: theme.spacing(4),
        paddingBottom: theme.spacing(4),
    },
    paper: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        padding: theme.spacing(2),
        backgroundColor: 'rgba(255, 255, 255, 0.85)',
        borderRadius: '10px',
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: "#3b5284",
        animation: '$shake 2s infinite',

    },
    '@keyframes shake': {
        '0%, 100%': { transform: 'rotate(0)' },
        '10%, 30%, 50%, 70%, 90%': { transform: 'rotate(5deg)' },
        '20%, 40%, 60%, 80%': { transform: 'rotate(-5deg)' },
    },
    form: {
        width: '100%',
        marginTop: theme.spacing(3),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
}));

export default function SignUp() {
    const classes = useStyles();
    const inputRef = useRef();
    let { showNotification: show } = useNotification();
    let [ok, setOk] = useState(false);

    const handleSignUpSubmit = async (e) => {
        e.preventDefault();
        let data = {};
        let formdata = new FormData(e.target);
        formdata.forEach((value, key) => {
            data[key] = value;
        });
        log(data)
        // 检查 data 中的字段是否为空
        for (let key in data) {
            if (data.hasOwnProperty(key) && (data[key] === null || data[key] === "")) {
                if (fieldMap[key].required) {
                    log(data)
                    show(fieldMap[key].display + "不能为空!", 'error');
                    return;
                } else {
                    delete data[key]
                }

            }
        }
        log(data)
        try {
            await signUp(data)
            show("成功!", "success", 2000)
            setOk(true)
        } catch (e) {
            show(getDataFromErrorOrDefault(e), "error", 2500)
        }
    }

    return (
        ok ? <Ok /> :
            <main className={classes.main}>
                <div className={classes.container}>
                    <CssBaseline />
                    <div className={classes.paper}>
                        <Avatar className={classes.avatar}>
                            <LockOutlined />
                        </Avatar>
                        <Typography component="h1" variant="h5">
                            注册
                        </Typography>
                        <form className={classes.form} onSubmit={handleSignUpSubmit} noValidate>
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6}>
                                    <TextField
                                        autoComplete="nname"
                                        name="nickname"
                                        variant="outlined"
                                        required={fieldMap["nickname"].required}
                                        fullWidth
                                        id="nickname"
                                        label={fieldMap["nickname"].display}
                                        autoFocus
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <TextField
                                        variant="outlined"
                                        required={fieldMap["region"].required}
                                        fullWidth
                                        id="region"
                                        label={fieldMap["region"].display}
                                        name="region"
                                        autoComplete="region"
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <FormControl fullWidth variant="outlined" required>
                                        <InputLabel id="gender-label">{fieldMap["gender"].display}</InputLabel>
                                        <Select
                                            labelId="gender-label"
                                            id="gender"
                                            required={fieldMap["gender"].required}
                                            name='gender'
                                            label={fieldMap["gender"].display}
                                        >
                                            <MenuItem defaultChecked value="男">男</MenuItem>
                                            <MenuItem value="女">女</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>

                                <Grid item xs={12}>
                                    <TextField
                                        variant="outlined"
                                        required={fieldMap["email"].required}
                                        fullWidth
                                        id="email"
                                        label={fieldMap["email"].display}
                                        name="email"
                                        autoComplete="email"
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <TextField
                                        variant="outlined"
                                        required={fieldMap["phone"].required}
                                        fullWidth
                                        id="phone"
                                        label={fieldMap["phone"].display}
                                        name="phone"
                                        autoComplete="tel"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        variant="outlined"
                                        required={fieldMap["password"].required}
                                        fullWidth
                                        name="password"
                                        label={fieldMap["password"].display}
                                        inputProps={{
                                            ref: inputRef
                                        }}
                                        type="password"
                                        id="password"
                                        autoComplete="current-password"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <FormControlLabel
                                        control={<Checkbox value="allowExtraEmails" color="primary" />}
                                        label="我想通过电子邮件接收通知"
                                    />
                                </Grid>
                            </Grid>
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                color="primary"
                                className={classes.submit}
                            >
                                注册
                            </Button>
                            <Grid container justifyContent="flex-end">
                                <Grid item>
                                    <Link to="/" state={{ login: true }} variant="body2">
                                        已经有账户？登录
                                    </Link>
                                </Grid>
                            </Grid>
                        </form>
                    </div>
                    <Box mt={5}>
                        <Copyright />
                    </Box>
                </div>
            </main>
    );
}
