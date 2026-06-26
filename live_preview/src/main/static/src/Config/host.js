import { Model } from "./config";

const TURN_HOST = Model == "debug" ? "localhost" : "ytycc.com";
const STURN_HOST = Model == "debug" ? "localhost" : "ytycc.com";

const CHAT_HOST = Model == "debug" ? "localhost:8000" : "ytycc.com:8000";
const GATEWAY_HOST = Model == "debug" ? "localhost:8080" : "ytycc.com:8080";
export {
    TURN_HOST, STURN_HOST, CHAT_HOST, GATEWAY_HOST
}