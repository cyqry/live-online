import HistoryList from "./HistoryList";
import Header from "./Header";
import { Outlet } from "react-router";

export default function AttentionAndHistory() {

    return (
        <div>
            <Header />
            <div style={{ paddingLeft: "18px", paddingTop: "20px", boxSizing: "border-box" }}>
                <Outlet />
            </div>
        </div>
    )
}