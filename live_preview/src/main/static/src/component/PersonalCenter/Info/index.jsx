import React from 'react';
import { Box, CircularProgress, Container, Grid } from '@mui/material';
import AvatarSection from './AvatarSection';
import UserInfoSection from './UserInfoSection';
import AccountSettingsSection from './AccountSettingsSection';
import { useEffect } from 'react';
import { useState } from 'react';
import { getUser } from '../../../Api/userApi';
import { useNavigate } from 'react-router';
import Loading from '../../Loading';
import { log } from '../../../Common/util';
const userProfile = {
    // 请根据实际情况填充数据
    avatar: 'avatar-url',
    nickname: '昵称',
    userId: '个性ID',
    gifts: [
        { icon: 'http://localhost:8083/img?name=gift1.png', name: '礼物1', amount: 5 },
        { icon: 'http://localhost:8083/img?name=gift1.png', name: '礼物2', amount: 10 },
        // 更多礼物
    ],
    basicInfo: [
        { label: '年龄', value: '25' },
        { label: '性别', value: '男' },
        { label: '所在地', value: '北京' },
        { label: '个性化签名', value: '这是我的个性签名' },
        // 更多基本信息
    ],
};

function Info() {
    const [changed, setChanged] = useState(false);

    const [userProfile, setUserProfile] = useState(null);
    const change = () => { setChanged(!changed) };
    const nav = useNavigate()


    useEffect(() => {

        async function run() {
            try {
                let user = await getUser();
                if (user)
                    setUserProfile(user)
                else {
                    nav("/", {
                        state: {
                            login: true
                        }
                    })
                    console.log("未登录")
                }
            } catch (e) {
                nav("/", {
                    state: {
                        login: true
                    }
                })
                log(e)
            }
        }
        run()
    }, [changed])

    return (
        !userProfile ?
            <Loading size={120} /> :
            <Grid container spacing={1} sx={{ marginTop: "0px", padding: "30px 50px", backgroundColor: "#fff", paddingTop: "0px", boxSizing: "border-box" }}>
                <Grid item xs={12}>
                    <AvatarSection userProfile={userProfile} change={change} />
                </Grid>
                <Grid item xs={12}>
                    <UserInfoSection userProfile={userProfile} change={change} />
                </Grid>
                <Grid item xs={12}>
                    <AccountSettingsSection isBindEmail={Boolean(userProfile.email)} isBindPhone={Boolean(userProfile.phone)} isAuthentication={Boolean(userProfile.idNumber)} change={change} />
                </Grid>
            </Grid>
    );
}

export default Info;