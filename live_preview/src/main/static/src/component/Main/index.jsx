import Live from "../Live";
import IndexPage from "../IndexPage";
import Anchor from "../Anchor";
import Test from "../z_Test";
import { BrowserRouter, Navigate, Route, Routes, useRoutes } from "react-router-dom";
import { Button, createTheme } from "@mui/material";
import TestContext from "../z_Test/TestContext";
import Header from "../Header";
import { ThemeProvider } from "@material-ui/styles";
import PersonalCenter from "../PersonalCenter";
import Info from "../PersonalCenter/Info";
import AttentionAndHistory from "../PersonalCenter/AttentionAndHistory";
import RoomManager from "../PersonalCenter/RoomManager";
import HistoryList from "../PersonalCenter/AttentionAndHistory/HistoryList";
import AttentionList from "../PersonalCenter/AttentionAndHistory/AttentionList";
import Watch from "../Watch";
import Classify from "../Classify";
import AnchorRegister from "../PersonalCenter/AnchorRegister";
import Search from "../Search";
import ClearBug from "../ClearBug";
import SignUp from "../SignUp";
import { NotificationProvider } from "../NotificationProvider";
import Report from "../PersonalCenter/Report";
import MyRoomAdmin from "../PersonalCenter/MyRoomAdmin";

export default function Main() {
    const theme = createTheme({
        palette: {
            primary: {
                main: '#00AEEC',
            },
            secondary: {
                main: '#f50057',
            },
        },
    });

    // let eles = useRoutes([
    //     { path: "/", element: <IndexPage /> },
    //     { path: "/v", element: <Live /> },
    //     { path: "/a", element: <Anchor /> },
    //     { path: "/test", element: <Test /> },
    //     { path: "/q", element: <Navigate to={"/"}/> }
    // ])
    return (
        <NotificationProvider >
            <ThemeProvider theme={theme} >
                <ClearBug />{/* 若不加该组件并放在所有组件之前,那么所有组件的className不会优先生效(改变了由className和自带样式className的顺序，导致自定义的className优先级低)*/}
                <BrowserRouter >
                    {/* 下面的所有路由都可以被一张路由表替代; 可替换为以下代码：{eles}  (这里eles没有写子路由，后面手动添上) */}
                    {/* 现在强制用Routes包裹路由，这样如果出现相同路径，只会匹配到第一个Route */}
                    <Header />
                    <Routes >
                        {/*路由识别不分大小写，可配置 caseSensitive从而区分*/}
                        <Route path="/i" element={<PersonalCenter />} children={[
                            <Route path="info" element={<Info />} />,
                            <Route path="sub" element={<AttentionAndHistory />} children={[
                                <Route path="subscribe" element={<AttentionList />} />,
                                <Route path="history" element={<HistoryList />} />,
                                <Route path="" element={<Navigate to={"/i/sub/subscribe"} replace={true} />} />,
                            ]} />,
                            <Route path="manager" element={<RoomManager />} />,
                            <Route path="registry" element={<AnchorRegister />} />,
                            <Route path="reportInfo" element={<Report />} />,
                            <Route path="myRoomAdmin" element={<MyRoomAdmin />} />,
                            <Route path="" element={<Navigate to={"/i/info"} replace={true} />} />,
                        ]} />
                        <Route path="/signup" element={<SignUp />} />
                        <Route path="/live" element={<Live />} />
                        <Route path="/class" element={<Classify />} />
                        <Route path="/search" element={<Search />} />
                        <Route path={"/"} element={<IndexPage />} />
                        <Route path={"/v/:roomId"} element={<Watch />} />
                        <Route path={"/anchor"} element={<Anchor />} />
                        {/* 依然是通过非严格匹配来实现二级路由的 */}
                        <Route path={"/test/:testParam"} element={<Test />} children={[<Route path='w' element={<Button size={"small"}  >RouteTest</Button>} />]} />
                        {/*               Navigate用于匹配上面都未匹配到，然后匹配到这里的情况，一旦渲染，会重定向页面到 'to'这个路径, to必填                */}
                        <Route path={"/q"} element={<Navigate to={"/"} />} />{/*Navigate一旦渲染就会重定向，就是重定向功能的组件，所以可以用于很多地方*/}
                        <Route path="/con" element={<TestContext />} />
                        <Route path="/*" element={<Navigate to={"/"} replace={true} />} />
                    </Routes>
                </BrowserRouter>
            </ThemeProvider>
        </NotificationProvider>
    )

}