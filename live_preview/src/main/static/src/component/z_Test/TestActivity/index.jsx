import React, { useState } from 'react';
import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { Button, Grid, Typography } from '@mui/material';

// 创建一个 keyframes 动画，用于定义图片从按钮到屏幕中央的移动
const moveAnimation = keyframes`
  from {
    opacity: 1;
    transform: translate(0, 0);
  }
  to {
    opacity: 0;
    transform: translate(50%, -50%);
  }
`;

// 创建一个 keyframes 动画，用于定义炸裂效果
const explodeAnimation = keyframes`
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
`;

// 使用 styled-components 创建一个包含动画的图片组件
const AnimatedImage = styled.img`
  animation: ${({ animation }) => animation};
`;

const AnimationExample = () => {
  const [animationInProgress, setAnimationInProgress] = useState(false);

  const handleButtonClick = () => {
    setAnimationInProgress(true);
    setTimeout(() => {
      setAnimationInProgress(false);
    }, 2000); // 这里设置延迟2秒，模拟炸裂动画结束后的状态
  };

  return (
    <Grid container direction="column" alignItems="center" spacing={2}>
      <Grid item>
        <Button
          variant="contained"
          color="primary"
          onClick={handleButtonClick}
          disabled={animationInProgress}
        >
          Start Animation
        </Button>
      </Grid>
      <Grid item>
        {animationInProgress ? (
          <AnimatedImage
            src="https://fileserver.cdn.huya.com/web_admin_propStreamerResource/4165e4b315564320997894d3e55a4541.png" // 替换为你的图片路径
            animation={`${moveAnimation} 1s ease-in-out, ${explodeAnimation} 0.5s 1s ease-in-out forwards`}
          />
        ) : (
          <Typography variant="body1">Click the button to start the animation</Typography>
        )}
      </Grid>
    </Grid>
  );
};

export default AnimationExample;
