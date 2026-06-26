// AdminList.js
import React from 'react';
import { useQuery } from 'react-query';
import {
    Box,
    Typography,
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    CircularProgress,
    Alert,
} from '@mui/material';
import { takeOfficeList } from '../../../Api/spaceApi';
import { getDataFromErrorOrDefault } from '../../../Common/util';
import Loading from '../../Loading';



const AdminList = () => {
    //        这里随便设置key
    let { data, isLoading, isError, error } = useQuery('adminsKey', takeOfficeList)

    if (isLoading) {
        return <Loading size={60} />;
    }

    if (isError) {
        return <Alert severity="error">Error: {getDataFromErrorOrDefault(error, "网络错误！")}</Alert>;
    }

    return (
        <Box>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>角色</TableCell>
                            <TableCell>任职时间</TableCell>
                            <TableCell>管理对象</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((admin, index) => {
                            return (
                                <TableRow key={index}>
                                    <TableCell>{admin.roleName}</TableCell>
                                    <TableCell>{admin.time}</TableCell>
                                    <TableCell>{admin.range}</TableCell>
                                </TableRow>
                            )
                        })}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default AdminList;
