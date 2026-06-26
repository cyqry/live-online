import { makeStyles } from "@material-ui/styles";

const useStyles = makeStyles((theme) => ({
    search: {
        backgroundColor: '#f1f1f1',
        textAlign: 'center',
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
        paddingBottom:"20px"
    },
    header: {
        display: "flex",
        justifyContent: "center",
        padding: '1rem',
        boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
    },
    resultHeader: {
        marginTop: "15px",
    },
    em: {
        color: '#ff2a00',
        padding: '0 3px',
        fontWeight: 700,
    },
    tag: {
        float: "left",
        color: '#666',
        marginLeft: "20px"
    },
    resultBody: {
        marginTop: "20px"
    }
}))
export default useStyles;