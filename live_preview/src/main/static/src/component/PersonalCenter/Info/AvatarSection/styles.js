import { makeStyles } from "@material-ui/styles";
export const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        padding: theme.spacing(2),
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
    },
    avatar: {
        width: 120,
        height: 120,
        marginRight: 30
    },
    nickname: {
        marginTop: theme.spacing(1),
    },
    personalityId: {
        marginTop: theme.spacing(0.5) + "!important",
        color: theme.palette.text.secondary,
    },
    currencies: {
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'center',
    },
    currency: {
        display: 'flex',
        alignItems: 'center',
        marginRight: theme.spacing(1),
    },
    recharge: {
        backgroundColor: "#f80",
        marginLeft: "20px"
    },
    currencyIcon: {
        width: 24,
        height: 24,
    },
    currencyName: {
        marginLeft: theme.spacing(0.5) + "!important",
        color: theme.palette.text.secondary,
    },
}));