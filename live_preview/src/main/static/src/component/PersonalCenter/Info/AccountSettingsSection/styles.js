import { makeStyles } from "@material-ui/styles";


export const useStyles = makeStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
    },
    dialogTitle: {
        textAlign: "center"
    },
    title: {
        paddingBottom: theme.spacing(1),
        borderBottom: `1px solid ${theme.palette.divider}`,
    },
    settings: {
        marginTop: theme.spacing(2),
    },
    settingItem: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: theme.spacing(1),
    },
    icon: {
        marginRight: theme.spacing(2),
    },
    settingInfo: {
        flexGrow: 1,
    },
    settingTitle: {
        fontWeight: theme.typography.fontWeightBold + "!important",
    },
    settingDescription: {
        color: theme.palette.text.secondary,
    },
    settingButton: {
        marginLeft: theme.spacing(2),
    },
    checkIcon: {
        position: 'absolute',
        right: '-5px',
        bottom: 0,
        backgroundColor: theme.palette.success.main,
        borderRadius: '50%',
        width: theme.spacing(2),
        height: theme.spacing(2),
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: theme.palette.common.white,
    },

    verifiedContainer: {
        textAlign: "center",
        backgroundColor: theme.palette.primary.main,
        width: '3.5em',
        boxSizing: "border-box",
        height: '1.2em',
        borderRadius: theme.spacing(1),
        paddingBottom: '22px',
    },
    verifiedText: {
        color: theme.palette.common.white,
        fontWeight: theme.typography.fontWeightMedium,
    },
}));
