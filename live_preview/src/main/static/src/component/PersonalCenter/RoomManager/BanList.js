// BanList.js
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
import { getOperations } from '../../../Api/spaceApi';
import { getDataFromErrorOrDefault } from '../../../Common/util';
import Loading from '../../Loading';

const BanList = () => {
    const { data: banData, isLoading, isError, error } = useQuery('bannedUsers', () => getOperations(0));


    if (isLoading) {
        return <Loading size={60} />;
    }

    if (isError) {
        return <Alert severity="error">Error: {getDataFromErrorOrDefault(error, "网络错误!")}</Alert>;
    }

    return (
        <Box>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>房间号</TableCell>
                            <TableCell>被封禁用户名</TableCell>
                            <TableCell>主播名称</TableCell>
                            <TableCell>封禁时间</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {banData.map((ban, index) => (
                            <TableRow key={index}>
                                <TableCell>{ban.roomId}</TableCell>
                                <TableCell>{ban.userNickname}</TableCell>
                                <TableCell>{ban.anchorNickname}</TableCell>
                                <TableCell>{new Date(ban.operatingTime).toLocaleString()}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default BanList;
