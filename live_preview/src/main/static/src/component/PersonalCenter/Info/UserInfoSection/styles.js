import { makeStyles } from "@material-ui/styles";

export const useStyles = makeStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
        paddingBottom:"0",
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderBottom: `1px solid ${theme.palette.divider}`,
    },
    title: {
        paddingBottom: theme.spacing(1),
    },
    info: {
        marginTop: theme.spacing(2),
        padding:"0px 20px"
    },
    infoRow: {
        display: 'flex',
        justifyContent: 'space-between',
        marginBottom: theme.spacing(1),
    },
    infoItem: {
        display: 'flex',
        alignItems: 'center',
        minWidth:"300px",
        maxWidth:"500px"
    },
    infoLabel: {
        fontWeight: theme.typography.fontWeightBold,
        marginRight:theme.spacing(1)
    },
    infoValue: {
        color: theme.palette.text.secondary,
    },
}));
