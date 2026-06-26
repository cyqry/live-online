import React, { useEffect, useState } from 'react';
import Authenticated from './Authenticated';
import Registration from './Registration';
import { CircularProgress } from '@mui/material';
import { getAnchor } from '../../../Api/userApi';
import { useNotification } from '../../NotificationProvider';
import Loading from '../../Loading';
import { log } from '../../../Common/util';

const AnchorRegister = () => {
    let { show } = useNotification()

    let [anchor, setAnchor] = useState(null);

    let [flag, setFlag] = useState(false)
    useEffect(() => {
        getAnchor().then(
            (data) => {
                data.isAnchor = true
                setAnchor(data)
            },
            (e) => {
                log(e)
                setAnchor({ isAnchor: false })
            }
        ).catch(e => {
            show("网络错误!", 'error', 3000)
        })
    }, [flag])
    let reLoadAnchorRegister = () => { setFlag(f => !f) }
    return (
        <div>
            {
                !anchor ? <Loading size={90} /> : anchor.isAnchor
                    ? <Authenticated anchor={anchor} />
                    : <Registration reLoadAnchorRegister={reLoadAnchorRegister} />
            }
        </div>
    );
};

export default AnchorRegister;
