import { Box, Button, Card, CardContent, Grid, Input, Stack, Typography } from "@mui/material";
import useStyles from "./styles";
import React, { useState } from "react";
import { height } from "../../../Common/util";
import { makeStyles } from "@material-ui/styles";


const GiftItem = ({ gift, onSendGift }) => {
    const classes = useStyles();
    const [active, setActive] = useState(false);
    const [giftCount, setGiftCount] = useState(1);
    const [inGift, setInGift] = useState(false);
    const handleDivMouseEnter = (gift) => {
        setActive(true);
    };

    const handleDivMouseLeave = () => {
        setActive(false);
    };
    const handleGiftCountChange = (event) => {
        setGiftCount(event.target.value);
    };

    const handleSendGift = () => {
        if (onSendGift) {
            onSendGift(gift, giftCount);
        }
        setActive(false);
        setGiftCount(1);
    };
    return (
        <div className={classes.giftComponent} style={{ position: "relative" }}
            onMouseEnter={() => handleDivMouseEnter(gift)}
            onMouseLeave={handleDivMouseLeave}>
            <img
                key={gift.id}
                src={gift.src}
                alt={gift.name}
                className={classes.gift}
            />
            <Card className={classes.giftCard}
                onMouseEnter={() => { setInGift(true) }}
                onMouseLeave={() => { setInGift(false) }}
            >
                <Grid container alignItems="center" justify="center">
                    <Grid paddingTop={"15px"} paddingLeft={"10px"} item xs={2.8}>
                        <img src={gift.src} alt={gift.name} />
                    </Grid>
                    <Grid paddingTop={"15px"} paddingRight={"10px"} item xs={9.2}>
                        <Stack direction={"row"}>
                            <Typography variant="h6" >
                                {gift.name}
                            </Typography>
                            <Typography color="textSecondary" sx={{ verticalAlign: "center", lineHeight: "32px", marginLeft: "5px", color: "pink" }}>
                                价格:{gift.price}
                            </Typography>
                        </Stack>

                        <Typography variant="body2" component="p" color={"blueviolet"}>
                            {gift.description}
                        </Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Stack spacing={1} direction={"row"} justifyContent={"center"} paddingTop={"10px"} paddingBottom={"10px"} bgcolor={"orange"} height={"2em"}>
                            <Input
                                className={classes.input}
                                type="number"
                                value={giftCount}
                                onChange={handleGiftCountChange}
                                inputProps={{ min: 1 }}
                            />
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleSendGift}
                            >
                                赠送
                            </Button>
                        </Stack>
                    </Grid>
                </Grid>
            </Card>
        </div>
    );
}
export default GiftItem;