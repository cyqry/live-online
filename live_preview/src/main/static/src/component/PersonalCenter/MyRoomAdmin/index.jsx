import { useNavigate } from "react-router";
import { useNotification } from "../../NotificationProvider";
import { useState, useEffect } from "react";
import { addRoomAdmin, deleteRoomAdmin, getRoomAdmins, getUserByPersonalityId } from "../../../Api/userApi";
import { getDataFromErrorOrDefault, log } from "../../../Common/util";
import { Button, IconButton, List, ListItem, ListItemSecondaryAction, ListItemText, TextField, Typography } from "@mui/material";
import { Add, Close, Search, Star } from "@mui/icons-material";

export default function MyRoomAdmin() {
    const navigate = useNavigate();
    const { show } = useNotification();

    const [personalityId, setPersonalityId] = useState("");
    const [searchResult, setSearchResult] = useState(null);
    const [myAdmins, setMyAdmins] = useState([]);

    useEffect(() => {
        getRoomAdmins()
            .then((res) => {
                setMyAdmins((prevAdmins) => [...prevAdmins, ...res]);
            })
            .catch((error) => {
                log(error);
            });
    }, []);

    const handleAddRoomAdmin = (user) => {
        addRoomAdmin(user.personalityId)
            .then((res) => {
                show("添加房管成功!", "success", 2000);
                setMyAdmins((prevAdmins) => [...prevAdmins, user]);
            })
            .catch((error) => {
                const errorMessage = getDataFromErrorOrDefault(error, "网络错误!");
                show(errorMessage, "error", 2300);
            });
    };

    const handleSearch = () => {
        getUserByPersonalityId(personalityId)
            .then((res) => {
                setSearchResult(res);
            })
            .catch((error) => {
                log(error);
            });
    };

    const handleRemoveAdmin = (personalityId) => {
        deleteRoomAdmin(personalityId)
            .then((res) => {
                show("删除成功", "success", 2000);
                setMyAdmins((prevAdmins) => prevAdmins.filter((admin) => admin.personalityId !== personalityId));
            })
            .catch((error) => {
                const errorMessage = getDataFromErrorOrDefault(error, "网络错误!");
                show(errorMessage, "error", 2300);
            });
    };

    return (
        <div style={{ padding: "25px" }}>
            <Typography variant="h6" sx={{ color: "#333", fontWeight: "bold", marginBottom: "20px", marginLeft: "-10px" }}>
                我的房管
            </Typography>

            {/* 搜索用户 */}
            <Typography variant="h6" sx={{ fontSize: "18px" }}>搜索用户</Typography>
            <div style={{ display: "flex", alignItems: "center", marginBottom: "10px" }}>
                <TextField
                    label="搜索"
                    value={personalityId}
                    onChange={(e) => setPersonalityId(e.target.value)}
                    variant="outlined"
                    margin="dense"
                    style={{ marginRight: "10px" }}
                />
                <Button variant="contained" color="primary" onClick={handleSearch} startIcon={<Search />}>
                    搜索
                </Button>
            </div>

            {/* 搜索结果 */}
            <div>
                <Typography variant="h6" sx={{ fontSize: "18px" }}>搜索结果</Typography>
                {!searchResult ? (
                    <div style={{ textAlign: "center", color: "#555", lineHeight: "100px", height: "100px" }}>暂无搜索结果~</div>
                ) : (
                    <List>
                        <ListItem>
                            <ListItemText primary={searchResult.nickname} secondary={`个性ID: ${searchResult.personalityId}`}
                            />
                            <ListItemSecondaryAction>
                                <Button startIcon={<Add />} onClick={() => handleAddRoomAdmin(searchResult)}>
                                    添加房管
                                </Button>
                            </ListItemSecondaryAction>
                        </ListItem>
                    </List>
                )}
            </div>

            {/* 我的房管 */}
            <div>
                <Typography variant="h6" sx={{ fontSize: "18px" }}>我的房管</Typography>
                {!myAdmins.length > 0 ? (
                    <div style={{ textAlign: "center", color: "#555", lineHeight: "100px", height: "100px" }}>还没有房管~</div>
                ) : (
                    <List>
                        {myAdmins.map((admin) => (
                            <ListItem key={admin.personalityId}>
                                <ListItemText primary={admin.nickname} secondary={`个性ID: ${admin.personalityId}`} />
                                <ListItemSecondaryAction>
                                    <IconButton edge="end" onClick={() => handleRemoveAdmin(admin.personalityId)}>
                                        <Close />
                                    </IconButton>
                                </ListItemSecondaryAction>
                            </ListItem>
                        ))}
                    </List>
                )}
            </div>
        </div>
    );
}
