import React from 'react';
import { Button } from '@mui/material';
import { clear } from '../live';

const EndStreamButton = ({ onEnd }) => {
    
    const handleEndStream = () => {
        if (onEnd) onEnd();
    };

    return (
        <Button onClick={handleEndStream} variant="contained" color="secondary">
            结束直播
        </Button>
    );
};

export default EndStreamButton;
