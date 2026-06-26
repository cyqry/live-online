import React from 'react';
import { Avatar, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grow, IconButton, Stack, Typography } from '@mui/material';
import { useStyles } from './styles';
import { Edit } from '@mui/icons-material';
import { useState } from 'react';
import EditDialog from './EditDialog';
import { useEffect } from 'react';
import { getImage } from '../../../../Api/storeApi';
import { base64ToSrcOrDefault, log } from '../../../../Common/util';
import RechargePage from './RechargePage';
import {  currenciesMap } from '../../../../Common/const';



const AvatarSection = ({ userProfile, change }) => {
  const classes = useStyles();
  let { avatar, nickname, personalityId, property, userId } = userProfile;
  let [editOpen, setEditOpen] = useState(false);
  let [base64, setBase64] = useState(null);

  try {
    property = JSON.parse(property)
  } catch (e) {
    property = undefined;
  }



  let [rechageOpen, setRechangeOpen] = useState(false);
  const handleRechageClick = (e) => {
    setRechangeOpen(true)
  }

  useEffect(() => {
    async function run() {
      try {
        if (avatar) {
          let base64 = await getImage(avatar)
          setBase64(base64)
        }
      } catch (e) {
        log(e)
      }
    }
    run()
  }, [])

  const handleEditClick = () => {
    setEditOpen(true)
  }

  const getCurrencyCount = (property, currencyId) => {
    if (property && property.currencies && property.currencies.filter) {
      let currencyPropertys = property.currencies.filter(currencyProperty => currencyProperty.currencyId == currencyId);
      if (currencyPropertys && currencyPropertys[0] && currencyPropertys[0].count) {
        return currencyPropertys[0].count
      }
    }
    return 0;
  }

  let src = base64ToSrcOrDefault(base64)
  return (
    <Box className={classes.root}>
      <Avatar alt={nickname} src={src} className={classes.avatar} />
      <Stack direction={"column"} spacing={2}>
        <Box>
          <Typography variant="h6" className={classes.nickname}>
            {nickname}
            <IconButton sx={{ float: 'right' }} onClick={handleEditClick}>
              <Edit />
            </IconButton>
            <EditDialog open={editOpen} setEditOpen={setEditOpen} oldUserProfile={{ ...userProfile, base64Src: src }} change={change} />
          </Typography>
        </Box>
        <Box>
          <Typography variant="subtitle1" className={classes.personalityId}>
            个性ID: {personalityId}
          </Typography>
        </Box>
        <Box className={classes.currencies}>
          {
           Array.from(currenciesMap.values()).map((currency, index) => (
              <Box key={index} className={classes.currency}>
                <img src={currency.icon} alt={currency.name} className={classes.currencyIcon} />
                <Typography variant="body2" className={classes.currencyName}>
                  {currency.name}: {getCurrencyCount(property, currency.currencyId)}
                </Typography>
              </Box>
            ))
          }
          <Button className={classes.recharge} onClick={handleRechageClick} >充值</Button>
          <Dialog open={rechageOpen}>
            <DialogContent style={{ padding: 0, overflow: "hidden" }}>
              <RechargePage closeRechange={() => setRechangeOpen(false)} />
            </DialogContent>
          </Dialog>
        </Box>
      </Stack >
    </Box >
  );
};

export default AvatarSection;
