import Stack from "@mui/material/Stack";
import HeaderButton from "./HeaderButton";
import * as React from "react";
import { randomInt, width } from "../../../Common/util";
import { Avatar } from "@mui/material";
import CategoryGrid from "./ShowClassify/CategoryGrid";

export default function StartNavigation() {
    const sx = {
        width: width(25)
    };

    return (
        <Stack sx={sx} spacing={5} direction={"row"}>
            <HeaderButton to={"/"} >首页</HeaderButton>
            <HeaderButton to={"/live"} >直播</HeaderButton>
            <HeaderButton to={"/class"} details={CategoryGrid} >分类</HeaderButton>
        </Stack>
    )
}