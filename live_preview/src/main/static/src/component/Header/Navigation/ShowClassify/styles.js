import { makeStyles } from "@material-ui/styles"

const useStyles = makeStyles((theme) => {
    return {
        categoryGrid: {
            backgroundColor: "white",
            borderTop: "solid 2px #4a90e2",
            borderRight: "solid 1px rgba(0, 0, 0, 0.1)",
            borderBottom: "solid 1px rgba(0, 0, 0, 0.1)",
            borderLeft: "solid 1px rgba(0, 0, 0, 0.1)",
            borderRadius: "5px",
            overflowY: "hidden",
            overflowX: "hidden",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.08)",
        },
        more: {
            width: "80%",
            borderRadius: '18px 18px',
            backgroundColor: '#f1f2f4',
            color: '#333',
            '&:hover': {
                backgroundColor: 'red',
                color: 'white',
            }
        }
    }
})

export default useStyles;
