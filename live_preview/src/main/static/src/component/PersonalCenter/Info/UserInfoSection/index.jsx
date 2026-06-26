import React, { useState } from 'react';
import { Box, Typography, IconButton } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import { useStyles } from './styles';
import { EditProfileDialog } from './EditProfileDialog';
import { fieldMap } from '../../../../Common/const';

const UserInfoSection = ({ userProfile, change }) => {
  const classes = useStyles();
  const { age, gender, region, signature } = userProfile;

  const basicInfo = [
    { label: fieldMap.age.display, value: age ? age : 0 },
    { label: fieldMap.gender.display, value: (gender == '男' || gender == '女') ? gender : "男" },
    { label: fieldMap.region.display, value: region ? region : <span style={{ color: "gray" }}>{"未填写详细地址"}</span> },
    { label: fieldMap.signature.display, value: signature ? signature : "您还未填写个性签名~" },
  ]

  const [dialogOpen, setDialogOpen] = useState(false);

  const handleEditClick = () => {
    setDialogOpen(true);
  };


  return (
    <Box className={classes.root}>
      <Box className={classes.header}>
        <Typography variant="h6" className={classes.title}>
          基本信息
        </Typography>
        <IconButton onClick={handleEditClick} size="small">
          <EditIcon />
        </IconButton>
      </Box>
      <Box className={classes.info}>
        {basicInfo.map((info, index, arr) => {
          if (index % 2 === 0) {
            return (
              <Box key={index} className={classes.infoRow}>
                <Box className={classes.infoItem}>
                  <Typography variant="subtitle1" className={classes.infoLabel}>
                    {info.label}:
                  </Typography>
                  <Typography variant="body1" className={classes.infoValue}>
                    {info.value}
                  </Typography>
                </Box>
                {arr[index + 1] && (
                  <Box className={classes.infoItem}>
                    <Typography variant="subtitle1" className={classes.infoLabel}>
                      {arr[index + 1].label}:
                    </Typography>
                    <Typography variant="body1" className={classes.infoValue}>
                      {arr[index + 1].value}
                    </Typography>
                  </Box>
                )}
              </Box>
            );
          }
          return null;
        })}
      </Box>
      <EditProfileDialog open={dialogOpen} setEditOpen={setDialogOpen} userProfile={userProfile} change={change} />
    </Box>
  );
};

export default UserInfoSection;
