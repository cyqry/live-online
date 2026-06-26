
// 选择输入框
// export default function ComboBox() {
//     let topFilms=[
//         {label:"！！！！",hhhh:111},
//         {label:"！！222！！",hh:222},
//         {label:"！111！！！",hh:333}
//     ];
//     return (
//         <Autocomplete
//             disablePortal
//             id="combo-box-demo"
//             options={topFilms}
//             sx={{ width: 300 }}
//             renderInput={(params)=> <TextField {...params} label="您必须从预选值中选一个" />}
//         />
//     );
// }


import * as React from 'react';
import Paper from '@mui/material/Paper';
import InputBase from '@mui/material/InputBase';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import DirectionsIcon from '@mui/icons-material/Directions';
import { height, width } from "../../../Common/util";
import { useLocation, useNavigate } from 'react-router';
import { useRef } from 'react';

export default function CenterInput() {
    const nav = useNavigate();
    const inputRef = useRef();
    const isSearch = useLocation().pathname.endsWith("/search");

    const toSearch = () => {
        if (inputRef.current.value && inputRef.current.value != '') {
            nav("/search?target=" + inputRef.current.value, {
                replace: false,
            })
        }
    }
    return (
        !isSearch ?
            <Paper
                component="form"
                sx={{ display: 'flex', width: width(15), height: height(5.7), borderRadius: width(3.5) }}
            >
                {/*<IconButton sx={{ p: '10px' }} aria-label="menu">*/}
                {/*    <MenuIcon />    //菜单图标,这里不适合*/}
                {/*</IconButton>*/}

                <InputBase
                    sx={{ ml: width(1), flex: 1 }}
                    placeholder="搜索直播间"
                    inputProps={{ ref: inputRef, inputMode: 'numeric', pattern: '[0-9]*', 'aria-label': '搜索直播间'/*这个参数给帮助某些辅助工具识别*/ }}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            toSearch(e)
                        }
                    }}
                />
                <IconButton type="button" aria-label="search" onClick={toSearch} >
                    <SearchIcon />
                </IconButton>
                {/*这是一个竖直的分隔线*/}
                <Divider sx={{ height: "inherit" }} orientation="vertical" />
                <IconButton color="primary" onClick={toSearch} aria-label="directions">
                    <DirectionsIcon />
                </IconButton>
            </Paper> :
            <></>
    );
}