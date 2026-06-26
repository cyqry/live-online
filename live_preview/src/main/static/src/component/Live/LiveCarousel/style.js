import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles((theme) => {
    return {
        carouselWrapper: {
            display: 'flex',
            maxWidth: "600px",
            minWidth:"450px",
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: "center",
            backgroundColor: '#f5f5f5',
            borderRadius: '10px',
            padding: '10px',
        },
        carouselTitle: {
            alignSelf: "start",
            marginBottom: '10px',
            color: '#3f3f3f',
            fontWeight: 'bold',
        },
        carouselContainer: {
            position: "relative",
            width: "400px",
            height: "240px",
            borderRadius: "10px",
            overflow: "hidden",
            boxShadow: "0 2px 8px rgba(0, 0, 0, 0.2)",
        },
        carousel: {
            height: "100%",
        },
        carouselItem: {
            position: "relative",
            display: "flex",
            flexDirection: "column",
            justifyContent: "flex-end",
            height: "100%",
        },
        carouselImage: {
            width: "100%",
            height: "100%",
            objectFit: "cover",
            boxSizing:"border-box",
            borderRadius: "10px",
        },

        imageCounter: {
            color: "#3f3f3f",
            fontWeight: "bold",
            fontSize: "18px",
            textAlign: "right",
        },
    }
})
export default useStyles;
