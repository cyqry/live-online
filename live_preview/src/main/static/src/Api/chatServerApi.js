import axios from "axios";
import { log } from "../Common/util";
import { CHAT_HOST } from "../Config/host";


let SCHEME = "https://"

async function isBanedPost(roomId, userId) {
    return (await chatRequest('get', "/isBanedPost", { roomId, userId })).data
}

async function banedPost(roomId, userId) {
    return await chatRequest('post', "/banedPost", { roomId, userId }, { form: true })
}

async function chatRequest(method, path, data, config = {}) {
    let { form = false } = config;
    log("url:", SCHEME + CHAT_HOST + (path.startsWith('/') ? path : ('/' + path)))
    const axiosConfig = {
        method,
        params: method == 'get' && data ? data : {},
        url: SCHEME + CHAT_HOST + (path.startsWith('/') ? path : ('/' + path)),
        headers: method.toLowerCase() == 'post' ? {
            "Content-Type": form ? "application/x-www-form-urlencoded" : "application/json"
        } : { "Content-Type": "application/x-www-form-urlencoded" },
        data: method == 'post' && data ? data : {},
    }
    log(axiosConfig)
    let res = await axios(axiosConfig)
    log("res", res)
    return res;
}

export { chatRequest, isBanedPost, banedPost }