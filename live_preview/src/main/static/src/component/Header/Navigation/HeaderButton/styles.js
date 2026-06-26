import { makeStyles } from "@material-ui/styles"

const useStyles = makeStyles((theme) => {
    return {
        whole: {
            position: "relative", height: "auto", width: "68px", display: "inline-block",
            '&:hover $details': {
                height: "320px",
                opacity: 1,
                padding: "5%",
                width: "auto",
            }
        },
        details: {
            transition: "height 0.5s",
            position: "absolute",
            top: "46px",
            left: "-105px",
            opacity: 0,
            padding: "0",
            height: "0",
            width: "0",
        },
    }
})

export default useStyles;
