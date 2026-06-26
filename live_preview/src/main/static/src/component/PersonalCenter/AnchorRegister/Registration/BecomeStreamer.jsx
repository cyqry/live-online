// BecomeStreamerForm.js
import React, { useState } from 'react';
import {
    Avatar,
    Button,
    Container,
    Grid,
    Paper,
    TextField,
    Typography,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    CircularProgress,
    Dialog,
    DialogContent,
} from '@mui/material';
import { Box } from '@mui/system';
import { styled } from '@mui/system';
import { ArrowBackIos, CreditCardOutlined, EmailOutlined, LockOutlined, PersonOutline } from '@mui/icons-material';
import { useEffect } from 'react';
import { getAllCategories } from '../../../../Api/spaceApi';
import { useNotification } from '../../../NotificationProvider';
import { fieldMap } from '../../../../Common/const';
import { createObjectForForm, getDataFromErrorOrDefault, log } from '../../../../Common/util';
import { registerAnchor } from '../../../../Api/userApi';
import Loading from '../../../Loading';
const FormContainer = styled(Container)(({ theme }) => ({
    marginTop: theme.spacing(4),
}));

const FormPaper = styled(Paper)(({ theme }) => ({
    padding: theme.spacing(4),
}));

const BecomeStreamer = ({ closeRegister, reLoadAnchorRegister }) => {

    const [category, setCategory] = useState({})

    let { firstLevel, secondLevel, roomItem } = category
    const [isReqLoading, setIsReqLoading] = useState(false);
    const [allCategories, setAllCategories] = useState();


    const { show } = useNotification();

    useEffect(() => {
        getAllCategories()
            .then(
                (data) => {
                    setAllCategories(data);
                },
                (e) => {
                    log(e)
                    show(getDataFromErrorOrDefault(e), "error", 3000)
                }
            ).catch((e) => {
                show(getDataFromErrorOrDefault(e), "error", 3000)
            })
    }, [])



    const handleSubmit = (e) => {
        e.preventDefault();
        log(firstLevel, secondLevel, roomItem)
        let data = createObjectForForm(e.target);
        if (!roomItem || !roomItem.id || roomItem == '')
            show("请选择类型！", "warning", 3000)
        data["roomItemCategoryId"] = roomItem.id;

        setIsReqLoading(true)
        registerAnchor(data).then(
            (res) => {
                show("申请成功!", "success", 2000)
                closeRegister()
                reLoadAnchorRegister()
            },
            (error) => {
                log(error)
                show("数据格式错误!", "error")
            }
        ).catch((e) => {
            log(e)
            show("网络错误!", "error", 3000)
            closeRegister()
        }).finally(() => {
            setIsReqLoading(false)
        })
    };

    const handleSecondLevelChange = (e) => {
        setCategory({ ...category, secondLevel: e.target.value, roomItem: undefined });
    }

    const handlefirstLevelChange = (e) => {
        setCategory({ ...category, firstLevel: e.target.value, secondLevel: undefined, roomItem: undefined });
    };

    return (
        !allCategories ? <Loading size={90} model='other' /> :
            <Paper>
                <Dialog open={true} fullWidth maxWidth="sm">
                    <DialogContent>
                        <FormContainer maxWidth="sm" style={{ marginTop: "0" }}>
                            <Typography variant="h4" gutterBottom sx={{ textAlign: "center", position: "relative" }} >
                                注册成为主播
                                <Button onClick={closeRegister} sx={{ position: "absolute", top: 0, left: -25 }}>
                                    <ArrowBackIos />
                                </Button>
                            </Typography>
                            <form onSubmit={handleSubmit}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6}>
                                        <TextField
                                            fullWidth
                                            label={fieldMap["realName"].display}
                                            variant="outlined"
                                            name={"realName"}
                                            InputProps={{
                                                startAdornment: <PersonOutline sx={{ marginRight: "7px" }} />,
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <TextField
                                            fullWidth
                                            type="password"
                                            label={fieldMap.password.display}
                                            variant="outlined"
                                            name={"password"}
                                            InputProps={{
                                                startAdornment: <LockOutlined sx={{ marginRight: "7px" }} />,
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12} >
                                        <TextField
                                            fullWidth
                                            label={fieldMap.idNumber.display}
                                            variant="outlined"
                                            name={"idNumber"}
                                            InputProps={{
                                                startAdornment: <CreditCardOutlined sx={{ marginRight: '7px' }} />,
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            multiline
                                            rows={4}
                                            label={fieldMap["description"].display}
                                            variant="outlined"
                                            name={"description"}
                                            placeholder="请简要介绍一下自己"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <FormControl fullWidth variant="outlined">
                                            <InputLabel>直播类型</InputLabel>
                                            <Select
                                                label="直播类型"
                                                onChange={handlefirstLevelChange}
                                            >
                                                {allCategories.firstLevels.map((first) => (
                                                    <MenuItem key={first.id} value={first}>
                                                        {first.name}
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <FormControl fullWidth variant="outlined" disabled={!firstLevel}>
                                            <InputLabel>直播子类型</InputLabel>
                                            <Select
                                                label="直播子类型"
                                                onChange={handleSecondLevelChange}
                                                disabled={!firstLevel}
                                            >
                                                {firstLevel &&
                                                    allCategories.secondLevels.filter(s => s.firstLevelId == firstLevel.id).map((second) => (
                                                        <MenuItem key={second.id} value={second}>
                                                            {second.name}
                                                        </MenuItem>
                                                    ))}
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <FormControl fullWidth variant="outlined" disabled={!secondLevel}>
                                            <InputLabel>直播项</InputLabel>
                                            <Select
                                                fullWidth
                                                label="直播项"
                                                onChange={(e) => setCategory({ ...category, roomItem: e.target.value })}
                                                disabled={!secondLevel}
                                            >
                                                {secondLevel &&
                                                    allCategories.roomItems.filter(r => r.secondLevelId == secondLevel.id).map((rItem) => (
                                                        <MenuItem key={rItem.id} value={rItem}>
                                                            {rItem.name}
                                                        </MenuItem>
                                                    ))}
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                    <Grid item xs={12} sx={{ textAlign: "center" }}>
                                        <Button
                                            sx={{ width: "8em" }}
                                            type="submit"
                                            variant="contained"
                                            color="primary"
                                            startIcon={isReqLoading ? <CircularProgress sx={{ color: "white" }} size={20} /> : <></>}
                                        >
                                            注册
                                        </Button>
                                    </Grid>
                                </Grid>
                            </form>
                        </FormContainer>
                    </DialogContent>
                </Dialog>
            </Paper >
    );
};

export default BecomeStreamer;
