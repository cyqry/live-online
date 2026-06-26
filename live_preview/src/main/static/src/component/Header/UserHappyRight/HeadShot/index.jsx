import { Avatar, Box, Card, CardContent, CardMedia, Typography } from "@mui/material"
import { Link, useLocation, useNavigate } from "react-router-dom"
import SignIn from "../../../SignIn"
import { useState, useEffect } from "react";
import { base64ToSrcOrDefault, getDataFromErrorOrDefault, log, width } from "../../../../Common/util";
import { getUser, logout } from "../../../../Api/userApi";
import { getImage } from "../../../../Api/storeApi";
import { useNotification } from "../../../NotificationProvider";
import { makeStyles } from "@material-ui/styles";


const useStyles = makeStyles(t => (
    {
        changeColor: {

            color: "#2c3e50",
            '&:hover': {
                color: "#ff7700"
            }
        }
    }
))

export default function HeadShot() {
    const classes = useStyles()
    let txDiameter = width(3.15);
    let [signIn, setSignIn] = useState(false);
    let [user, setUser] = useState(null);
    let [base64, setBase64] = useState(null);
    let { state } = useLocation();
    let navigate = useNavigate();
    let { show } = useNotification()

    let [inAvatar, setInAvatar] = useState()
    let [inCard, setInCard] = useState()

    useEffect(() => {

        async function exec() {
            try {
                let user = await getUser();
                if (user) {
                    setUser(user)
                    if (user.avatar) {
                        let base64 = await getImage(user.avatar)
                        setBase64(base64)
                    }
                }

                else {
                    if (state && state.login) {
                        setSignIn(true)
                    }
                }
            } catch (e) {
                log(e);
                console.log("未登录")
            }

        }
        exec()

    }, [])

    const handleAvatarClick = () => {
        if (user) {
            log("user")
            navigate("/i", {})
            return
        }
        if (signIn == false)
            setSignIn(true)
    }
    const handLogout = (e) => {
        e.preventDefault();
        logout().then(
            res => {
                window.location.reload()
            },
            (e) => {
                show(getDataFromErrorOrDefault(e), "error", 2000)
            }
        ).catch((e) => {
            show(getDataFromErrorOrDefault(e), "error", 2000)
        })
    }


    let src = base64ToSrcOrDefault(base64)
    return (
        <>
            <div style={{ position: "relative", width: txDiameter, height: txDiameter, marginLeft: "14px" }} onMouseEnter={() => { setInAvatar(true) }} onMouseLeave={() => { setInAvatar(false) }}>
                <Avatar
                    alt="这是头像"
                    style={{ cursor: "pointer", }}
                    src={src}
                    onClick={handleAvatarClick}
                />
                {(inAvatar || inCard) && user &&
                    <Card sx={{ border: "1px solid #ddd", textAlign: "center", backgroundColor: "white", position: "absolute", bottom: "-172px", right: "-125px", width: "300px", height: "175px" }} onMouseEnter={() => { setInCard(true) }} onMouseLeave={() => { setInCard(false) }} >
                        <div style={{ position: "relative", width: "100%", height: "100%" }}>
                            <Avatar
                                alt="头像"
                                sx={{ width: "100px", height: "100px", margin: "15px auto 5px auto", cursor: "pointer" }}
                                src={src}
                                onClick={handleAvatarClick}
                            />
                            <Typography onClick={handleAvatarClick} className={classes.changeColor} variant="h5" sx={{ fontSize: "18px", cursor: "pointer", color: "#545454" }}>{user.nickname}</Typography>

                            <div
                                onClick={handLogout}
                                className={classes.changeColor}
                                style={{
                                    display: "inline-block"
                                }}>
                                <a
                                    style={{
                                        position: "absolute",
                                        right: "28px",
                                        width: "24px",
                                        cursor: "pointer",
                                        height: "36px",
                                        fontSize: "12px",
                                        top: "-5px",
                                        lineHeight: "36px",
                                        background: 'url(https://staticlive.douyucdn.cn/common/douyu/images/header-bar/header-out-icon.png?20161229) no-repeat left 10px'
                                    }}
                                    className={classes.changeColor}
                                ></a>
                                <span
                                    style={{
                                        position: "absolute",
                                        right: "8px",
                                        width: "24px",
                                        cursor: "pointer",
                                        height: "36px",
                                        fontSize: "12px",
                                        top: "-5px",
                                        lineHeight: "36px",
                                    }}>登出</span>
                            </div>
                        </div>
                    </Card>}
            </div>

            <SignIn open={signIn} onBackClick={() => { setSignIn(false) }} />
        </>
    )
}