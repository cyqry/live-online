import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    MenuItem,
    Box,
    IconButton,
    Typography,
} from '@mui/material';
import { AddAPhoto } from '@mui/icons-material';
import { Close } from '@mui/icons-material';
import { report } from '../../../../Api/spaceApi';
import { useNotification } from '../../../NotificationProvider';
import { base64ToSrcOrDefault, getDataFromErrorOrDefault } from '../../../../Common/util';
import { useRef } from 'react';

const ReportDialog = ({ open, onClose, roomId }) => {
    const [reason, setReason] = useState('');
    const [details, setDetails] = useState('');
    const [screenshots, setScreenshots] = useState([]);
    const [contact, setContact] = useState('');
    let { show } = useNotification()
    let imageRef = useRef()
    const handleReasonChange = (event) => {
        setReason(event.target.value);
    };

    const handleDetailsChange = (event) => {
        setDetails(event.target.value);
    };
    const handleScreenshotRemove = (index) => {
        const newScreenshots = screenshots.filter((_, i) => i !== index);
        setScreenshots(newScreenshots);
        imageRef.current.value = ''; // 重置 input 元素的值
    };

    const handleScreenshotChange = (event) => {
        const files = Array.from(event.target.files);

        if (screenshots.length + files.length <= 3) {
            Promise.all(files.map(file => {
                return new Promise((resolve, reject) => {
                    const reader = new FileReader();

                    reader.onload = () => {
                        const base64String = reader.result.split(',')[1];
                        resolve(base64String);
                    };

                    reader.onerror = (error) => {
                        reject(error);
                    };

                    reader.readAsDataURL(file);
                });
            }))
                .then(base64Strings => {
                    const updatedScreenshots = [...screenshots, ...base64Strings];
                    setScreenshots(updatedScreenshots);
                })
                .catch(error => {
                    show('文件格式错误!', "error", 2000);
                });
        } else {
            show('最多上传三张图片', "warning", 2300);
        }
    };

    const handleContactChange = (event) => {
        setContact(event.target.value);
    };

    const handleSubmit = () => {

        report({ roomId, reason, detail:details, screenshotBase64s: screenshots, contactWay: contact }).then(
            (suc) => {
                show("举报成功!请等待管理员审核", "success", 2500)
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误！"), "error", 2300)
            }
        ).catch(
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误！"), "error", 2300)
            }
        )
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>举报</DialogTitle>
            <DialogContent>
                <div style={{ height: "5px" }}></div>
                <TextField
                    fullWidth
                    select
                    label={"原因"}
                    value={reason}
                    onChange={handleReasonChange}
                    required
                >
                    <MenuItem value="垃圾信息">垃圾信息</MenuItem>
                    <MenuItem value="骚扰">骚扰</MenuItem>
                    <MenuItem value="不当内容">不当内容</MenuItem>
                </TextField>
                <TextField
                    fullWidth
                    multiline
                    rows={4}
                    margin="normal"
                    label="详情"
                    value={details}
                    onChange={handleDetailsChange}
                    required
                />
                <Box mt={2} display="flex" flexWrap="wrap">
                    <b>上传截图:</b>
                    {screenshots.map((screenshot, index) => (
                        <Box key={index} position="relative" width={100} height={100} mr={1} mb={1}>
                            <img
                                src={base64ToSrcOrDefault(screenshot)}
                                alt="screenshot"
                                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                            />
                            <IconButton
                                size="small"
                                style={{ position: 'absolute', top: '-10px', right: '-10px' }}
                                onClick={() => handleScreenshotRemove(index)}
                            >
                                <Close />
                            </IconButton>
                        </Box>
                    ))}
                    {screenshots.length < 3 && (
                        <>
                            <Box>
                                <input
                                    accept="image/*"
                                    style={{ display: 'none' }}
                                    ref={imageRef}
                                    type="file"
                                    id='screenshot-upload'
                                    multiple
                                    onChange={handleScreenshotChange}
                                />
                                <label htmlFor="screenshot-upload">
                                    <IconButton component="span">
                                        <AddAPhoto />
                                    </IconButton>
                                </label>
                            </Box>
                        </>
                    )}
                </Box>
                <TextField
                    fullWidth
                    margin="normal"
                    label="联系方式"
                    value={contact}
                    onChange={handleContactChange}
                    required
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>取消</Button>
                <Button onClick={handleSubmit} color="primary">
                    提交
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ReportDialog;
