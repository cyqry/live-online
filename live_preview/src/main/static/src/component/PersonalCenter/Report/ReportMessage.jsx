import React from 'react';
import {
    TextField,
    MenuItem,
    Box,
} from '@mui/material';
import { parseAvatarToSrc } from '../../../Api/api';

const ReportMessage = ({ report }) => {

    let { reason, detail, screenshotPaths, contactWay } = report
    return (
        <div style={{ width: "100%" }}>
            <TextField
                fullWidth
                label={"原因"}
                value={reason}
                required
            >
            </TextField>
            <TextField
                fullWidth
                multiline
                rows={4}
                margin="normal"
                label="详情"
                value={detail}
                required
            />
            <Box mt={2} display="flex" flexWrap="wrap">
                <b style={{marginRight:"10px"}}>证据截图:</b>
                {screenshotPaths.map((screenshot, index) => (
                    <Box key={index} position="relative" width={100} height={100} mr={1} mb={1}>
                        <img
                            src={parseAvatarToSrc(screenshot)}
                            alt="screenshot"
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                    </Box>
                ))}
            </Box>
            <TextField
                fullWidth
                margin="normal"
                label="联系方式"
                value={contactWay}
                required
            />
        </div>
    );
};

export default ReportMessage;