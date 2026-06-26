import React, { useState } from 'react';
import {
  Card,
  CardContent,
  Avatar,
  Typography,
  Button,
  Grid,
  List,
  ListItem,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  IconButton,
} from '@mui/material';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { makeStyles } from '@material-ui/styles';
import ReportMessage from '../ReportMessage';
import { parseAvatarToSrc } from '../../../../Api/api';
import { removeReport } from '../../../../Api/spaceApi';
import { useNotification } from '../../../NotificationProvider';
import { getDataFromErrorOrDefault } from '../../../../Common/util';

const useStyles = makeStyles((theme) => ({
  card: {
    marginBottom: theme.spacing(2),
    boxShadow: theme.shadows[2],
  },
  avatar: {
    marginRight: theme.spacing(2),
  },
  handledButton: {
    color: '#888888',
    fontWeight: 'bold',
  },
  backButton: {
    marginRight: theme.spacing(1),
  },
  enterRoomButton: {
    color: '#5c6ac4',
    fontWeight: 'bold',
    textTransform: 'none',
    marginLeft: theme.spacing(1),
  },
  reporter: {
    height: "50px",
    lineHeight: "50px",
    cursor: "pointer",
    "&:hover": {
      backgroundColor: "#f5f5f5",
    },
  },
}));

const ReportInfo = ({ roomId, avatar, anchorNickname, reports }) => {
  const classes = useStyles();
  const [isHandled, setIsHandled] = useState(false);
  const [selectedReport, setSelectedReport] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const { show } = useNotification()

  const handleButtonClick = () => {
    removeReport(roomId).then(
      (res) => {
        setIsHandled(true);
      }, (e) => {
        show(getDataFromErrorOrDefault(e, "网络错误!", "error", 2000))
      }
    ).catch((e) => {
      show(getDataFromErrorOrDefault(e, "网络错误!", "error", 2000))
    })
  };

  const handleReportClick = (report) => {
    setSelectedReport(report);
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  const handleEnterRoomClick = () => {
    // TODO: Handle enter room logic
  };

  return (
    <Card className={classes.card}>
      <CardContent sx={{ marginLeft: "20px" }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item>
            <Typography variant="subtitle1" gutterBottom>
              <span>主播:</span>
            </Typography>
          </Grid>
          <Grid item>
            <Avatar alt={anchorNickname} src={parseAvatarToSrc(avatar)} className={classes.avatar} />
          </Grid>
          <Grid item>
            <Typography variant="subtitle1" gutterBottom>
              房间号: {roomId}
            </Typography>
            <Typography variant="subtitle1" gutterBottom>
              <span>昵称:{anchorNickname}</span>
            </Typography>
          </Grid>
          <Grid item>
            <IconButton onClick={handleEnterRoomClick} className={classes.backButton}>
              <ArrowForwardIcon color="primary" />
              <Typography variant="subtitle1" className={classes.enterRoomButton}>
                进入房间
              </Typography>
            </IconButton>
          </Grid>
        </Grid>
        <Grid container>
          {reports.map((report) => (
            <Grid item md={6} key={report.id} onClick={() => handleReportClick(report)}>
              <ListItem className={classes.reporter}>
                <ListItemText primary={report.text} secondary={`举报人: ${report.userNickname}`} />
              </ListItem>
            </Grid>
          ))}
        </Grid>
        <Button
          variant="outlined"
          className={isHandled ? classes.handledButton : ''}
          disabled={isHandled}
          onClick={handleButtonClick}
        >
          {isHandled ? '已处理' : '我已处理'}
        </Button>
      </CardContent>

      {/* Dialog to display report details */}
      <Dialog open={dialogOpen} onClose={handleDialogClose} PaperProps={{ sx: { width: '500px', margin: '0 auto', padding: "30px" } }}>
        {selectedReport && (
          <>
            <DialogTitle>举报详情</DialogTitle>
            <ReportMessage report={selectedReport} />
            <DialogActions>
              <Button onClick={handleDialogClose}>关闭</Button>
            </DialogActions>
          </>
        )}
      </Dialog>

    </Card>
  );
};

export default ReportInfo;
