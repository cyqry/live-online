import React, { useState } from 'react';
import { Button } from '@mui/material';

const LiveControl = () => {
  const [isLive, setIsLive] = useState(false);

  const handleLiveToggle = () => {
    setIsLive(!isLive);
  };

  return (
    <Button variant="contained" color={isLive ? 'secondary' : 'primary'} onClick={handleLiveToggle}>
      {isLive ? '暂停直播' : '开始直播'}
    </Button>
  );
};

export default LiveControl;
