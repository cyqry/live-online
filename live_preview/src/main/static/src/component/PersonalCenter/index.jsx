import React from 'react';
import { Box, Container, Grid } from '@mui/material';
import AvatarSection from './Info/AvatarSection';
import UserInfoSection from './Info/UserInfoSection';
import AccountSettingsSection from './Info/AccountSettingsSection';
import Sidebar from './Sidebar';
import RoomManager from './RoomManager';
import { Outlet, useNavigate } from 'react-router';
import { useEffect } from 'react';
import { useState } from 'react';
import { getIsAnchor, getUser } from '../../Api/userApi';


function PersonalCenter() {
    let [user, setUser] = useState()
    let [isAnchor, setIsAnchor] = useState(false)
    let nav = useNavigate()
    useEffect(() => {
        getIsAnchor().then(
            (res) => {
                setIsAnchor(res)
            })

        getUser().then(
            res => {
                setUser(res)
            },
            e => {
                nav("/")
            }
        )
    }, [])
    return (
        <Container style={{ backgroundColor: "#f2f5f6", display: "flex", padding: "0px", flexDirection: "row", marginTop: "30px", minHeight: "700px", maxWidth: "1160px" }}  >
            <Sidebar user={user} isAnchor={isAnchor} />
            <Box style={{ flex: 1 }}>
                <Outlet />
            </Box>
        </Container>
    );
}

export default PersonalCenter;