// OperationLog.js
import React from 'react';
import {  useMutation, useQuery, useQueryClient } from 'react-query';
import DeleteIcon from '@mui/icons-material/Delete';
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
    IconButton,
    Tooltip,
} from '@mui/material';
import { getDataFromErrorOrDefault } from '../../../Common/util';
import { getOperations, removeOperating } from '../../../Api/spaceApi';
import { useNotification } from '../../NotificationProvider';
import Loading from '../../Loading';

const OperationLog = () => {
    const { data: operationLogData, isLoading, isError, error } = useQuery('operationLogs', () => getOperations(-1));
    let { show } = useNotification()
    const queryClient = useQueryClient();
    const removeReportMutation = useMutation(removeOperating);

    if (isLoading) {
        return <Loading size={80} />;
    }

    if (isError) {
        return <Alert severity="error">Error: {getDataFromErrorOrDefault(error, "网络错误!")}</Alert>;
    }

    const handleRemove = (recordId) => {
        removeReportMutation.mutate(recordId, {
            onSuccess: () => {
              // 在删除成功后，更新缓存数据并触发重新渲染该组件
              queryClient.invalidateQueries('operationLogs');
            },
            onError: (error) => {
              show(getDataFromErrorOrDefault(error, "网络错误!"), "error", 2000);
            }
          });
    }

    return (
        <Box>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>操作类型</TableCell>
                            <TableCell>操作目标</TableCell>
                            <TableCell>主播昵称</TableCell>
                            <TableCell>操作时间</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {operationLogData.map((log, index) => (
                            <TableRow key={index}>
                                <TableCell>{log.operating}</TableCell>
                                <TableCell>{log.type == 1 ? '房间号：' + log.roomId : '用户：' + log.userNickname}</TableCell>
                                <TableCell>{log.anchorNickname}</TableCell>
                                <TableCell>{new Date(log.operatingTime).toLocaleString()}</TableCell>
                                <TableCell>
                                    <Tooltip title="移除">
                                        <IconButton onClick={() => { handleRemove(log.recordId) }}>
                                            <DeleteIcon />
                                        </IconButton>
                                    </Tooltip>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default OperationLog;
