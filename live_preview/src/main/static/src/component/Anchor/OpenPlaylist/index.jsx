import React, { useState, useEffect } from 'react';
import {
    Button,
    Container,
    Grid,
    Paper,
    TextField,
    Typography,
    RadioGroup,
    FormControlLabel,
    Radio,
    FormControl,
    FormLabel,
    Select,
    MenuItem,
    CircularProgress
} from '@mui/material';
import { createRoom, getRoomInfo, getRoomTag } from '../../../Api/spaceApi';
import { useNavigate } from 'react-router';
import { useNotification } from '../../NotificationProvider';
import { getDataFromErrorOrDefault, log } from '../../../Common/util';
import Loading from '../../Loading';

function OpenPlaylist({ setRoomExtra }) {
    const [coverFile, setCoverFile] = useState(null);
    const [roomDesc, setRoomDesc] = useState('');
    const [broadcastType, setBroadcastType] = useState('camera');
    const [cameraDevices, setCameraDevices] = useState([]);
    const [selectedCamera, setSelectedCamera] = useState('');
    const [roomInfo, setRoomInfo] = useState();

    let { show } = useNotification()
    let { nav } = useNavigate()

    useEffect(() => {
        getRoomInfo()
            .then(
                data => {
                    setRoomInfo(data)
                },
                (e) => {
                    log(e)
                    nav("/", {})
                }
            ).catch((e) => {
                log(e)
                show("网络错误!", "error")
            })

        const getCameraDevices = async () => {
            const devices = await navigator.mediaDevices.enumerateDevices();
            const videoDevices = devices.filter(device => device.kind === 'videoinput');
            setCameraDevices(videoDevices);
            setSelectedCamera(videoDevices[0]?.deviceId || '');
        };
        getCameraDevices();
    }, []);

    const handStartCLick = async () => {
        try {
            log(coverFile)
            if (!coverFile) {
                show("请选择封面", "warning", 2500)
                return
            }
            if (!roomDesc || roomDesc == '') {
                show("请填写房间标题", "warning", 2500)
                return
            }

            await createRoom({ roomTitle: roomDesc, coverBase64: coverFile.replace(/^data:image\/\w+;base64,/, '') })
            log("创建房间成功！", roomInfo)
            let roomTag = await getRoomTag(roomInfo.id);
            setRoomExtra({ roomInfo, roomTag, choose: broadcastType == "camera" ? 1 : 0 })
        } catch (e) {
            show(getDataFromErrorOrDefault(e, "开启直播失败!"), "error",2500)
        }

    }

    const handleCoverUpload = (e) => {
        const reader = new FileReader();
        reader.readAsDataURL(e.target.files[0]);
        reader.onloadend = () => {
            setCoverFile(reader.result);
        };

    };

    const handleRoomDescChange = (e) => {
        setRoomDesc(e.target.value);
    };

    const handleBroadcastTypeChange = (e) => {
        setBroadcastType(e.target.value);
    };

    const handleCameraChange = (e) => {
        setSelectedCamera(e.target.value);
    };



    return (
        !roomInfo ? <Loading style={{ marginTop: "50px" }} model="other" size={400} /> :
            <Container maxWidth="sm" >
                <Paper elevation={3} style={{ padding: '2rem' }}>
                    <Typography variant="h4" gutterBottom sx={{ color: "orange" }}>
                        开启你的直播吧
                    </Typography>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <input
                                accept="image/*"
                                capture
                                style={{ display: 'none' }}
                                id="cover-upload"
                                type="file"
                                onChange={handleCoverUpload}
                            />
                            <label htmlFor="cover-upload">
                                <Button variant="contained" component="span">
                                    上传封面
                                </Button>
                            </label>
                            {coverFile && (
                                <>
                                    <Typography sx={{ marginTop: "10px", fontSize: "18px", color: "#333" }}>预览:</Typography>
                                    <img src={coverFile} alt="封面预览" style={{ width: '90%', height: (439.2 * 9 / 16) + "px", marginTop: '1rem' }} />
                                </>
                            )}
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="房间标题"
                                variant="outlined"
                                value={roomDesc}
                                onChange={handleRoomDescChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl component="fieldset">
                                <FormLabel component="legend">直播类型</FormLabel>
                                <RadioGroup
                                    row
                                    aria-label="broadcastType"
                                    name="broadcastType"
                                    value={broadcastType}
                                    onChange={handleBroadcastTypeChange}
                                >
                                    <FormControlLabel
                                        value="camera"
                                        control={<Radio />}
                                        label="摄像头直播"
                                    />
                                    <FormControlLabel
                                        value="screen"
                                        control={<Radio />}
                                        label="屏幕共享直播"
                                    />
                                </RadioGroup>
                            </FormControl>
                        </Grid>
                        {broadcastType === 'camera' && (
                            <Grid item xs={12}>
                                <FormControl fullWidth variant="outlined">
                                    <FormLabel component="legend">摄像头设备</FormLabel>
                                    <Select
                                        value={selectedCamera}
                                        onChange={handleCameraChange}
                                        label="摄像头设备"
                                    >
                                        {cameraDevices.map((device, index) => (
                                            <MenuItem key={index} value={device.deviceId}>
                                                {device.label || '未知摄像头设备'}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                        )}
                        <Grid item xs={12}>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handStartCLick}
                                style={{ marginRight: '1rem' }}
                            >
                                开始直播
                            </Button>
                        </Grid>
                    </Grid>
                </Paper>
            </Container>
    );
}

export default OpenPlaylist;
