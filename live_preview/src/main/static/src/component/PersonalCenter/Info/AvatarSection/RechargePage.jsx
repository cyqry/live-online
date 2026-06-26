import React, { useState } from 'react';
import {
    Container,
    Grid,
    Typography,
    Button,
    Box,
    Card,
    CardContent,
    CardMedia,
    Stack,
    Avatar,
} from '@mui/material';
import { motion } from 'framer-motion';
import { makeStyles } from '@material-ui/styles';
import { base64ToSrcOrDefault, log } from '../../../../Common/util';
import { recharge } from '../../../../Api/userApi';
import { useNotification } from '../../../NotificationProvider';
import { currencies, currenciesMap } from '../../../../Common/const';

const useStyles = makeStyles((theme) => ({
    card: {
        padding: theme.spacing(2),
        background: 'white',
    },
    media: {
        height: 0,
        paddingTop: '56.25%', // 16:9
    },
    button: {
        margin: theme.spacing(1),
    },
}));

const rechargeVariants = {
    initial: { opacity: 0, scale: 0.8 },
    animate: { opacity: 1, scale: 1 },
    exit: { opacity: 0, scale: 0.8 },
};

const currencyOptions = [
    {
        name: '炫币',
        //前端才有这个名字，后端只有id
        field: 'xb',//id为1
        amounts: [100, 200, 500, 1000],
        icon: currenciesMap.get(1).icon,
    },
    {
        name: '炫点',
        field: 'xd',
        amounts: [1000, 2000, 5000, 10000],
        icon: currenciesMap.get(2).icon,
    },
];

const RechargePage = ({ closeRechange }) => {
    const classes = useStyles();
    const [selectedCurrency, setSelectedCurrency] = useState({ xb: undefined, xd: undefined });

    const { show } = useNotification();

    const handleCurrencySelect = (currency, amount) => {
        setSelectedCurrency({
            ...selectedCurrency,
            [currency]: selectedCurrency[currency] === amount ? undefined : amount,
        });
    };
    const handleSubmit = () => {
        log(selectedCurrency);

        let currencies = [];
        Object.entries(selectedCurrency).map(
            (obj, index) => {
                log(obj)
                if (obj[0] && obj[1])
                    currencies.push({ currencyId: obj[0] == "xb" ? 1 : 2, count: obj[1] })
            })
            log(currencies)

        recharge({ currencies })
            .then(
                (res) => {
                    console.log(res.data)
                    show("充值成功!", "success", 2000)
                    window.location.reload()
                    closeRechange()
                },
                (e) => {
                    console.log(e)
                    show("充值失败!", "error")
                }
            )
    };

    const handleCancel = () => {
        setSelectedCurrency({ 炫币: undefined, 炫点: undefined })
        closeRechange()
    }

    return (
        <motion.div
            style={{
                width: "100%",
                height: "100%",
            }}
            initial="initial"
            animate="animate"
            exit="exit"
            variants={rechargeVariants}
        >
            <Card className={classes.card}>
                <CardContent>
                    <Grid container direction="column" alignItems="center" spacing={2}>
                        <Grid item>
                            <Typography variant="h5" >
                                货币充值
                            </Typography>
                        </Grid>
                        <Grid item>
                            <Box>
                                {currencyOptions.map(({ name, field, amounts, icon }) => (
                                    <Grid container alignItems="center" key={name}>
                                        <Grid item>
                                            <Avatar src={icon} alt={name} />
                                        </Grid>
                                        {amounts.map((option) => (
                                            <Grid item key={option}>
                                                <Button
                                                    variant={
                                                        selectedCurrency[field] === option
                                                            ? 'contained'
                                                            : 'outlined'
                                                    }
                                                    color="primary"
                                                    className={classes.button}
                                                    onClick={() => handleCurrencySelect(field, option)}
                                                >
                                                    {option} {name}
                                                </Button>
                                            </Grid>
                                        ))}
                                    </Grid>
                                ))}
                            </Box>
                        </Grid>
                        <Grid item>
                            <CardMedia
                                className={classes.media}
                                image="https://source.unsplash.com/random"
                                title="Decorative Image"
                            />
                        </Grid>
                        <Grid item>
                            <Button
                                variant="outlined"
                                color="secondary"
                                className={classes.button}
                                onClick={handleCancel}
                            >
                                取消
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                className={classes.button}
                                onClick={handleSubmit}
                                disabled={!selectedCurrency['xb'] && !selectedCurrency['xd']}
                            >
                                确认充值
                            </Button>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        </motion.div>
    );
};

export default RechargePage;
