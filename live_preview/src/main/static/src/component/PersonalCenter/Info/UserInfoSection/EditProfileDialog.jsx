import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, FormControl, TextField, Typography, Container, InputLabel, Select, Snackbar, MenuItem, Slide, Stack, Grid } from '@mui/material';
import { makeStyles } from '@material-ui/styles';
import { Close, Save } from '@mui/icons-material';
import { fieldMap } from '../../../../Common/const';
import { updateUser } from '../../../../Api/userApi';
import { Form } from 'react-router-dom';
import { useNotification } from '../../../NotificationProvider';
import { createObjectForForm, filterBlank } from '../../../../Common/util';


const useStyles = makeStyles((theme) => ({
    title: {
        textAlign: "center"
    },

    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
    },
    formControl: {
        margin: theme.spacing(1),
    },
    input: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(2),
    },
    button: {
        width: "101px",
        marginTop: theme.spacing(4),
        marginBottom: theme.spacing(4),
    },
}));




export const EditProfileDialog = ({ open, userProfile, setEditOpen, change }) => {
    let classes = useStyles();

    return (
        <Dialog open={open} onClose={() => {
            setEditOpen(false)
        }}>
            <DialogTitle className={classes.title}>编辑基本信息信息</DialogTitle>
            <DialogContent style={{ paddingTop: "1ch" }}>
                <EditProfile change={change} oldProfile={userProfile} setEditOpen={setEditOpen} />
            </DialogContent>
        </Dialog>
    );
};

function EditProfile({ oldProfile, setEditOpen, change }) {
    const classes = useStyles();
    let { show } = useNotification();

    // 使用一个状态对象来存储所有表单字段的值
    const [formValues, setFormValues] = useState({
        age: oldProfile.age ? oldProfile.age : undefined,
        gender: oldProfile.gender,
        region: oldProfile.region,
        signature: oldProfile.signature,
    });



    const handleSubmit = (e) => {
        e.preventDefault();

        let data = createObjectForForm(e.target)
        data = filterBlank(data)

        async function run() {
            try {
                await updateUser(data);
                // change(); //通知父父父组件重新加载数据
                window.location.reload();
                setEditOpen(false);
            } catch (e) {
                show("数据格式错误!", 'error');
            }

        }
        run();
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setEditOpen(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormValues((prevValues) => ({ ...prevValues, [name]: value }));
    };
    return (
        <Container maxWidth="sm" className={classes.container}>
            <form onSubmit={handleSubmit}>
                <Stack justifyContent={"space-between"} direction={'row'}>
                    <FormControl style={{ minWidth: '45%' }} fullWidth className={classes.formControl}>
                        <TextField
                            className={classes.input}
                            label={fieldMap['age'].display}
                            type="number"
                            name="age"
                            value={formValues.age}
                            onChange={handleChange}
                            InputProps={{
                                inputProps: { onInput: (e) => { e.target.value = e.target.value.replace(/[^\d]/g, '') } }
                            }}
                            InputLabelProps={{
                                style: { opacity: 1 },
                                shrink: true,
                            }}
                        />
                    </FormControl>
                    <FormControl style={{ minWidth: '45%' }} fullWidth className={classes.formControl}>
                        <Select
                            label=""
                            name="gender"
                            value={(formValues.gender == '男' || formValues.gender == '女') ? formValues.gender : '男'}
                            onChange={handleChange}
                            className={classes.input}
                        >
                            <MenuItem value={'男'}>男</MenuItem>
                            <MenuItem value={'女'}>女</MenuItem>
                        </Select>
                    </FormControl>
                </Stack>
                <FormControl style={{ minWidth: '100%' }} fullWidth className={classes.formControl}>
                    <TextField
                        className={classes.input}
                        label={fieldMap['region'].display}
                        name="region"
                        value={formValues.region}
                        onChange={handleChange}
                        InputLabelProps={{
                            shrink: true,
                        }}
                    />
                </FormControl>
                <FormControl style={{ minWidth: '100%' }} fullWidth className={classes.formControl}>
                    <TextField
                        className={classes.input}
                        label={fieldMap["signature"].display}
                        multiline
                        rows={4}
                        name="signature"
                        value={formValues.signature}
                        onChange={handleChange}
                        InputLabelProps={{
                            shrink: true,
                        }}
                    />
                </FormControl>
                <Grid container spacing={14} justifyContent="center">
                    <Grid item>
                        <Button
                            className={classes.button}
                            color="secondary"
                            variant='outlined'
                            size="large"
                            onClick={handleClose}
                        >
                            关闭
                        </Button>
                    </Grid>
                    <Grid item>
                        <Button
                            className={classes.button}
                            variant="contained"
                            color="primary"
                            size="large"
                            type="submit"
                            startIcon={<Save />}
                        >
                            保存
                        </Button>
                    </Grid>
                </Grid>

            </form>
        </Container>

    );
}



