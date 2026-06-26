import { Height } from "@mui/icons-material";
import { height, multiplyAndGetLength, width } from "../../Common/util";

const HeaderWidth = width(62);
const HeaderHeight = height(18);
const VideoHeight = multiplyAndGetLength(HeaderWidth, 0.5625);
const SideBarWidth = width(23);
const GiftHeigth = height(6);
const DanMuHeigth = multiplyAndGetLength(VideoHeight, 1);
export { HeaderHeight, HeaderWidth, VideoHeight, SideBarWidth, GiftHeigth,DanMuHeigth }