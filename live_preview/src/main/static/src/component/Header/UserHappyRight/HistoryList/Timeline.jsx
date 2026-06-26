import React from 'react';
import { Box, Typography } from '@mui/material';

const Timeline = ({ height }) => {
    return (
        <Box
            sx={{
                width: '2px',
                height: height,
                backgroundColor: 'grey.500',
            }}
        />
    );
};

export default Timeline;
