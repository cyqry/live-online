import Stack from "@mui/material/Stack";
import * as React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { log, randomInt } from "../../../../Common/util";
import { createRef } from "react";
import { Button, Input, TextField } from "@mui/material";
import { useRef } from "react";

export default function JumpLink() {
  let inputRef = useRef()
  let nav = useNavigate()

  return (
    <Stack spacing={1} direction={"row"}>
      <Input inputRef={inputRef} />
      <Button onClick={() => {
        log(inputRef.current.value)
        nav("/v/" + inputRef.current.value,
          {
            state: {
              isJump: true
            }
          })
      }} >
        一个直播间
      </Button>

      {/* end属性,若路径对应路由组件的子路由匹配上了，且该Link组件依然被渲染，那么也让 isActive为false*/}
      <NavLink end to={"/anchor/123456"}>
        开播
      </NavLink>

      {/*  这里传了个路径参数,route6还直接有个state属性来传 state参数 */}
      <NavLink to={"/test/hhhhh?name=yty"} state={{}} >测试组件</NavLink>

      <NavLink to="/con">测试Context</NavLink>
    </Stack>
  )
}