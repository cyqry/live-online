import React from 'react';
import { ListItem, ListItemText, Avatar, Box } from '@mui/material';
import { green } from '@mui/material/colors';
import { base64ToSrcOrDefault } from '../../../../Common/util';
import { useNavigate } from 'react-router';
import { makeStyles } from '@material-ui/styles';

const useStyles = makeStyles((t) => (
    {
        item: {
            cursor: "pointer",
            "&:hover": {
                backgroundColor: "#cfd9da"
            }
        }
    }
))


const LiveHistoryListItem = ({ history, avatarMap }) => {

    let nav = useNavigate()
    const classes = useStyles()
    return (
        <ListItem  className={classes.item} onClick={() => {
            nav("/v/" + history.roomId, {
                state: {
                    isJump: true
                }
            })
        }}>
            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    marginRight: '16px',
                }}
            >
                <Avatar
                    src={avatarMap && avatarMap.get(history.roomId) ? avatarMap.get(history.roomId) : base64ToSrcOrDefault()}
                    sx={{
                        border: history.online
                            ? `2px solid ${green[500]}`
                            : '2px solid transparent',
                    }}
                />
                <ListItemText
                    primary={history.anchorNickname}
                    secondary={history.online ? '在线' : '离线'}
                    sx={{
                        marginLeft: '8px',
                        marginRight: '16px',
                    }}
                />
            </Box>
            <ListItemText

                primary={history.domain}
            />
        </ListItem>
    );
};

export default LiveHistoryListItem;
