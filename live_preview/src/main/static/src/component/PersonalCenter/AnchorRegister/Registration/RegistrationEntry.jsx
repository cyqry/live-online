import React from "react";
import { Avatar, Box, Button, Container, Typography } from "@mui/material";
import { makeStyles } from "@material-ui/styles";
import iconVerify from "../../../../static/img/icon-peo-2fe4b.min.png"
const useStyles = makeStyles((theme) => ({
  container: {
    paddingTop:"30px",
    minHeight: "100vh",
  },
  title: {
    fontWeight: 500,
    fontSize: 24,
    color: '#333',
    borderLeft: '3px solid #f80',
    padding: '0 0 0 7px',
    maxwidth: '350px',
    display: 'inline-block',
    textAlign: 'left',
    height: '25px',
    lineHeight: '25px',
  },
  auth: {
    width: "95%",
    boxSizing: 'border-box',
    marginTop: theme.spacing(2),
    padding: '10px 0 35px 125px',
    position: 'relative',
    borderBottom: '1px solid #efefef',
    marginBottom: '35px',
    overflow: 'hidden',
  },
  avatar: {
    width: '80px',
    height: '80px',
    position: 'absolute',
    left: 0,
    top: 0,
  },
  button: {
    float: 'right',
    margin: '25px 30px auto 0',
  },
  introduce: {
    display: "inline-block",
    width: "70%"
  },
  tag: {
    fontSize: '20px',
    color: "#333",
    fontWeight: 800,
  },
  state: {
    background: '#e7e7e7',
    color: '#999',
    fontSize: '12px',
    fontWeight: 300,
    padding: '1px 10px',
    borderRadius: '10px',
    margin: '0 0 0 5px',
    display: "inline-block",
    verticalAlign: 'text-bottom',
  },
  desc: {
    fontSize: '12px',
    color: '#666',
    lineHeight: '22px',
    textAlign: 'justify',
  }
}));

const RegistrationEntry = ({ onClick }) => {
  const classes = useStyles();
  return (
    <Container className={classes.container} >
      <Typography component="h3" className={classes.title}>
        加入直播平台
      </Typography>
      <Box className={classes.auth}>
        <Avatar alt={"直播认证"} src={iconVerify} className={classes.avatar} />
        <Box className={classes.introduce}>
          <Typography component="h4" className={classes.tag}>直播认证
            <Typography component="span" className={classes.state}>未认证</Typography>
          </Typography>
          <Typography component="p" className={classes.desc}>直播认证通过后（未满18周岁无法认证），可实现开直播、提现佣金等操作。</Typography>
        </Box>
        <Button
          variant="contained"
          color="primary"
          className={classes.button}
          onClick={onClick}
        >
          注册成为主播
        </Button>
      </Box>
    </Container>
  );
};

export default RegistrationEntry;
