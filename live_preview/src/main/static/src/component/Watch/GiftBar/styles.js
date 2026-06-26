import { makeStyles } from "@material-ui/styles";
import { height } from "../../../Common/util";
const useStyles = makeStyles((theme) => ({
    container: {
        display: 'flex',
        flexDirection: "row-reverse",
        padding: theme.spacing(1),
        paddingRight: theme.spacing(3),
        backgroundColor: "#f8fbfb",
        borderRadius: "0px 0px 10px 10px",
    },
    giftComponent: {
        marginRight: "10px",
        '&:hover $giftCard': {
            width: "290px",
            height: "115px",
        }
    },
    gift: {
        padding: theme.spacing(1),
        borderRadius: '50%',
        cursor: 'pointer',
        '&:hover': {
            borderColor: theme.palette.secondary.main,
        },
    },
    giftCard: {
        display: 'inline-block',
        width: "0",
        height: "0",
        position: 'absolute',
        zIndex: 1000,
        marginTop: theme.spacing(2),
        transition: 'width 0.75s,height 0.5s',
        bottom: "50px",
        left: "-10px"
    },
    input: {
        width: '30%',
        marginBottom: theme.spacing(1),
    },
}));

export default useStyles;