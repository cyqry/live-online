import { Box } from "@mui/material";
import Category, { LiveCategory } from "../Classify/Category";
import LiveCarousel from "./LiveCarousel";

export default function Live() {
    return (
        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
            <LiveCarousel style={{ marginBottom: "15px", marginTop: "25px" }} />
            <LiveCategory width={"95%"} style={{ paddingTop: "15px" }} />

            <div style={{ width: "100%", height: "290px" }}></div>
        </Box>
    )
}