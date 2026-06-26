import { makeStyles } from "@material-ui/styles";
import { Box, Button, Container, Typography } from "@mui/material";
import successImage from "../../static/img/checked.png";
const useStyles = makeStyles((theme) => ({
    successContainer: {
        display: "flex",
        flexDirection: "column",
        textAlign:"center",
        alignItems: "center",
        justifyContent: "start",
        height: "80vh",
    },
    successImage: {
        width: 200,
        marginTop: theme.spacing(6),
        marginBottom: theme.spacing(3),
    },
    successMessage: {
        color: "red",
        textAlign: "center",
        paddingLeft:"21px"
    }
}));

export const Ok = () => {
    const classes = useStyles();

    return (
        <Container className={classes.successContainer}>
            <img src={successImage} alt="Success" className={classes.successImage} />
            <Typography variant="h4" className={classes.successMessage} gutterBottom>
                注册成功！
            </Typography>
            <Typography variant="body1"  gutterBottom>
                欢迎您加入我们的直播平台！
            </Typography>
            <Box mt={3}>
                <Button variant="contained" color="primary" href="/">
                    返回首页
                </Button>
            </Box>
        </Container>
    );
};