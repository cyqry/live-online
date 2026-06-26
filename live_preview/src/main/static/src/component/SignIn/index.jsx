import { makeStyles } from '@material-ui/styles';
import { KeyboardBackspace, LockOutlined } from '@mui/icons-material';
import { Avatar, Box, Button, Checkbox, Container, CssBaseline, Dialog, FormControlLabel, Grid, IconButton, TextField, Typography } from '@mui/material';
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn } from '../../Api/userApi';
import { useNotification } from '../NotificationProvider';
import { getDataFromErrorOrDefault, log } from '../../Common/util';
function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" href="https://mui.com/">
        ytycc.com
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(4),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
    textAlign: "center",
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  bak: {
    position: "absolute",
    top: 0,
    left: '0',
    float: 'left',
    margin: '25px 0px 0px 10px',
  },
}));

export default function SignIn({ open = true, onBackClick }) {
  const classes = useStyles();
  let { showNotification: show } = useNotification();
  const formSubmitHandle = async (e) => {
    e.preventDefault();
    let data = {
      remember: 0
    };
    let formdata = new FormData(e.target);
    formdata.forEach((value, key) => {
      data[key] = value;
    });
    // 检查 data 中的字段是否为空
    for (let key in data) {
      if (data.hasOwnProperty(key) && (data[key] === null || data[key] === "")) {
        show((key === "account" ? "账号" : key === "password" ? "密码" : "") + "不能为空!", 'error', 3000);
        return;
      }
    }
    log(data)
    try {
      await signIn(data)
      show("登录成功!", "success", 1500)
      setTimeout(() => {
        window.location.href = '/'
      }, 500)
    }
    catch (e) {
      show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
    }
  }


  return (
    <Dialog open={open} sx={{ zIndex: 1212 }} >
      <Container component="main" maxWidth="xs" sx={{ position: "relation" }}>
        <IconButton className={classes.bak} onClick={onBackClick}>
          <KeyboardBackspace />
        </IconButton>
        <CssBaseline />

        <div className={classes.paper}>
          <Avatar className={classes.avatar}>
            <LockOutlined />
          </Avatar>
          <Typography component="h1" variant="h5">
            登录
          </Typography>
          <form className={classes.form} onSubmit={formSubmitHandle} noValidate>
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              id="account"
              label="邮箱或手机号"
              name="account"
              autoComplete="u_account"
              autoFocus
            />
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              name="password"
              label="密码"
              type="password"
              id="password"
              autoComplete="current-password"
            />
            <FormControlLabel
              control={<Checkbox inputProps={{ name: "remember" }} value={1} color="primary" />}
              label="记住我"
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
            >
              登录
            </Button>
            <Grid container>
              <Grid item md={12}>
                <Link to="/signup" style={{ float: "right" }} onClick={onBackClick} variant="body2">
                  {"还没有账户? 注册一个"}
                </Link>
              </Grid>
            </Grid>
          </form>
        </div>
        <Box mt={8}>
          <Copyright />
        </Box>
      </Container>
    </Dialog>
  );
}