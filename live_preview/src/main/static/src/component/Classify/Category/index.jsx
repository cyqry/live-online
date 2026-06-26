import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar, Typography, Button, Grid, Paper, Box, Chip, CardMedia, CardContent, Card, CircularProgress, Avatar, IconButton } from '@mui/material';
import { createStyles, makeStyles } from '@material-ui/styles';
import { PlayArrow, PlayArrowOutlined } from '@mui/icons-material';
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { parseAvatarToSrc } from '../../../Api/api';
import { base64ToSrcOrDefault, getDataFromErrorOrDefault, log } from '../../../Common/util';
import { getAllCategories, getRoomItems, selectSecondRoom } from '../../../Api/spaceApi';
import { useNotification } from '../../NotificationProvider';
import { getImage } from '../../../Api/storeApi';
import Loading from '../../Loading';


const useStyles = makeStyles((theme) => ({
    appBar: {
        marginBottom: "2%",
        backgroundColor: 'transparent',
        boxShadow: 'none',
    },
    title: {
        marginRight: "2%",
        fontSize: "26px",
    },
    chip: {

    },
    paper: {
        padding: "2%",
        textAlign: 'center',
        color: "red",
    },
    card: {
        width: '90%',
        backgroundColor: "white",
        height: 200,
        cursor: "pointer"
    },
    media: {
        height: 140,
    },
    content: {
        color: "#333",
        fontSize: "15px",
        textAlign: 'center',
        height: 30,
        '&:hover': {
            color: "#a0c83b"
        }
    },
}));


const defaultCategories = [{ id: -1, name: '全部' }];

const LiveCategory = ({ width, style }) => {

    let [roomItems, setRoomItems] = useState([])
    let [chooseRoomItem, setChooseRoomItem] = useState('全部')
    let [displayRooms, setDisplayRooms] = useState([])
    const { show } = useNotification()

    let location = useLocation();
    const classes = useStyles();
    let [p, setP] = useSearchParams()
    let secondId = p.get('s')
    let secondName = p.get('sName')
    if (!secondName || secondName == '') {
        secondName = "全部直播"
    }
    if (!secondId || secondId == '') {
        //由于没有设置专门默认，所以这里干脆设置默认为lol的id
        secondId = 19
        secondName = "英雄联盟"
    }


    useEffect(() => {

        selectSecondRoom(secondId).then(
            (res) => {
                setDisplayRooms(res)
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误！"), "error", 2000)
            }
        ).catch(
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误！"), "error", 2000)
            }
        )

        getRoomItems(secondId).then(
            (res) => {
                setRoomItems(res)
            },
            (e) => {
                log(e)
                setRoomItems([])
            }
        ).catch(e => {
            show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
        })
    }, [secondId, secondName])

    const handleItemClick = (name) => {
        setChooseRoomItem(name)
    }


    roomItems = [...defaultCategories, ...roomItems];

    //todo 改为 param
    const categories = location && location.state && location.state.liveCategory ? location.state.liveCategory : defaultCategories;
    const clazz = location && location.state && location.state.clazz ? location.state.claszz : "全部"
    return (
        <div style={{ width, paddingLeft: "15%", ...style, boxSizing: "border-box" }} >
            <Box display="flex" alignItems="center" className={classes.appBar}>
                <Typography variant="h6" className={classes.title}>
                    {secondName}
                </Typography>
                {roomItems.map((roomItem, index) => (
                    <div style={{ marginLeft: "1%" }} onClick={() => {
                        handleItemClick(roomItem.name)
                    }} key={index}>
                        <Chip
                            label={roomItem.name}
                            clickable
                            color="primary"
                            variant="outlined"
                            sx={{ color: roomItem.name == chooseRoomItem ? "white" : "", backgroundColor: roomItem.name == chooseRoomItem ? "#a0c83b" : "" }}
                            className={classes.chip}
                        />
                    </div>
                ))}
            </Box>
            <Grid container spacing={3}>
                {(chooseRoomItem == "全部" ? displayRooms : (displayRooms.filter(r => r.roomItemCategoryName == chooseRoomItem))).map((room) => (
                    <Grid item xs={12} sm={8} md={3} key={room.id}>
                        <RoomSearch room={room} width={275} />
                    </Grid>
                ))}
            </Grid>
        </div >
    );
};



const ClassfiyCategory = ({ isLiveCategory = true, width, style }) => {
    const [allCategories, setAllCategories] = useState({ firstLevels: [], secondLevels: [], roomItems: [] })
    const { show } = useNotification()
    //默认是 '全部'的id
    const [value, setValue] = useState(-1);
    let location = useLocation();
    const classes = useStyles();

    useEffect(() => {
        getAllCategories().then(
            (res) => {
                setAllCategories(res)
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
            }
        ).catch(e => {
            show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
        })
    }, [])

    const firstLevels = [...defaultCategories, ...allCategories.firstLevels];

    let displaySecondLevels = value == -1 ? allCategories.secondLevels : allCategories.secondLevels.filter(s => s.firstLevelId == value)
    //todo 改为 param
    const categories = location && location.state && location.state.liveCategory ? location.state.liveCategory : defaultCategories;
    const clazz = location && location.state && location.state.clazz ? location.state.claszz : "全部"
    return (
        <div style={{ width, ...style }} >
            <Box display="flex" alignItems="center" className={classes.appBar}>
                <Typography variant="h6" className={classes.title}>
                    全部{isLiveCategory ? "直播" : "分类"}
                </Typography>
                {firstLevels.map((firstLevel, index) => (
                    <div
                        key={index}
                        style={{ marginLeft: "1%" }}
                        onClick={() => {
                            setValue(firstLevel.id);
                        }}>
                        <Chip
                            label={firstLevel.name}
                            clickable
                            color="primary"
                            sx={{ backgroundColor: value == firstLevel.id ? "#f80" : "", color: value == firstLevel.id ? "white" : "", cursor: "pointer" }}
                            variant="outlined"
                            className={classes.chip}
                        />
                    </div>
                ))}
            </Box>
            <Grid container spacing={3}>
                {displaySecondLevels.map((secondLevel) => (
                    <Grid item xs={12} sm={6} md={2} key={secondLevel.id} >
                        <CategoryCard secondLevel={secondLevel} />
                    </Grid>
                ))}
            </Grid>
        </div >
    );
};

function OldCategoryCard({ id }) {
    const classes = useStyles();
    return <></>
    let [[loading, room], setList] = useState([true, {}]);

    useEffect(() => {

        let timer = setTimeout(() => {
            room.imageUrl = "https://via.placeholder.com/150";
            room.name = "房间" + id;
            setList([false, room])

        }, 1000 * id)

        return () => {
            clearTimeout(timer);
        };
    }, [])

    return (
        loading ? <CircularProgress className={classes.card} />
            : <Card className={classes.card}>
                <CardMedia
                    className={classes.media}
                    image={room.imageUrl}
                    title={room.name}
                />
                <CardContent className={classes.content}>
                    <Typography gutterBottom variant="h6" component="div">
                        {room.name}
                    </Typography>
                </CardContent>
            </Card>
    )

}


//根据id加载类型信息
function CategoryCard({ secondLevel }) {
    const classes = useStyles();
    const [base64, setBase64] = useState()
    const { show } = useNotification()
    const nav = useNavigate()
    useEffect(() => {
        getImage(secondLevel.imagePath).then(
            (res) => {
                setBase64(res)
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
            }
        ).catch((e) => {
            show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
        })

    }, [])
    // let [[loading, room], setList] = useState([true, {}]);

    // useEffect(() => {

    //     room.imageUrl = "https://via.placeholder.com/150";
    //     room.name = "房间" + id;
    //     setList([false, room])

    //     return () => {
    //         clearTimeout(timer);
    //     };
    // }, [])

    let src = base64ToSrcOrDefault(base64)
    return (<Card className={classes.card} onClick={() => {
        nav(`/live/?s=${secondLevel.id}&sName=${secondLevel.name}`)
    }}   >
        <CardMedia
            className={classes.media}
            image={src}
            title={secondLevel.name}
        />
        <CardContent className={classes.content}>
            <Typography gutterBottom variant="h6" component="div">
                {secondLevel.name}
            </Typography>
        </CardContent>
    </Card>
    )

}



const useRoomStyles = makeStyles((theme) => ({
    card: {
        maxWidth: "345px",
        maxWidth: "345px",
        overflow: 'hidden',
        boxShadow: theme.shadows[2],
        '&:hover': {
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
        },
        '&:hover $playButton': {
            opacity: 1,
            transform: 'translate(-50%, -50%) scale(2)',
        },
        '&:hover $media': {
            opacity: 0.5,
        },
    },
    media: {
        position: 'relative',
        transition: 'opacity 0.3s ease',
    },
    avatar: {
        marginRight: "3%"
    },
    roomName: {
        display: "inline-block",
        float: "left",
        fontSize: '15px',
        color: '#333',
        fontWeight: 500,
        marginLeft: "10px"
    },
    roomCategory: {

        display: "inline-block",
        float: "right",
        fontSize: '12px',
        color: '#888',
    },
    userName: {
        fontSize: '0.875rem',
        color: '#9c27b0',
    },
    popularity: {
        fontSize: '0.875rem',
        color: '#f44336',
    },
    cardContent: {
        paddingTop: "1%",
        paddingLeft: "1%",
        paddingBottom: "1% !important",
    },
    playButton: {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%) scale(3)',
        opacity: 0,
        transition: 'opacity 0.3s ease, transform 0.3s ease',
    }
}));

/**
 *  后端对应RoomSearch
 */

const RoomSearch = ({ room, width = 270 }) => {
    const classes = useRoomStyles();
    width = Number(width.toString().endsWith("px") ? width.toString().substring(0, width.indexOf("px")) : width);
    return (
        <Card className={classes.card}>
            {/* todo 携带state */}
            <Link to={'/v/' + room.roomId} state={{ isJump: true }} style={{ textDecoration: 'none' }}>
                <CardMedia
                    className={classes.media}
                    sx={{
                        objectFit: 'cover',
                        width: width + "px",
                        height: "" + ((width * 9) / 16) + "px"
                    }}
                    image={parseAvatarToSrc(room.coverSrc)}
                    title={room.title}
                >
                    <IconButton className={classes.playButton} color="primary">
                        <PlayArrow fontSize="large" sx={{ color: "white" }} />
                    </IconButton>
                </CardMedia>
            </Link>
            <CardContent className={classes.cardContent}>
                <Box sx={{ height: "24px", marginTop: "5px" }}>
                    <Typography gutterBottom variant="h6" lineHeight={"24px"} component="div" className={classes.roomName}>
                        {room.title}
                    </Typography>
                    <Typography gutterBottom variant="h6" lineHeight={"24px"} component="div" className={classes.roomCategory}>
                        {room.secondLevelCategoryName}
                    </Typography>
                </Box>
                <Grid container alignItems="center" direction={"row"} >
                    <Grid item marginRight={"4%"}>
                        <Avatar
                            alt={room.anchorNickname}
                            src={room.anchorAvatar ? parseAvatarToSrc(room.anchorAvatar) : base64ToSrcOrDefault()}
                            className={classes.avatar}
                            sizes='(max-width: 30px) 100vw, 
                            (max-width: 40px) 75vw, 
                            35px'
                            imgProps={{
                                sx: {
                                }
                            }}
                        />
                    </Grid>
                    <Grid item >
                        <Typography variant="subtitle1" className={classes.userName}>{room.anchorNickname}</Typography>
                    </Grid>
                    <Grid item xs />
                    <Grid item>
                        <Typography variant="subtitle1" className={classes.popularity}>{room.hot} 热度</Typography>
                    </Grid>
                </Grid>
            </CardContent>
        </Card >
    );
};

export { RoomSearch, ClassfiyCategory, LiveCategory }