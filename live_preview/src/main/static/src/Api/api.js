import axios from "axios";
import { GATEWAY_HOST } from "../Config/host";
import defalutAvatarImage from "../static/img/OIP.jpg"
import { log } from "../Common/util";
const SCHEME = "https://"
axios.defaults.withCredentials = true
// axios.defaults.headers.post['Content-Type'] = 'application/json; charset=utf-8';
// axios.defaults.crossDomain = true
async function request(server, method, path, data, config = {}) {
    if (!server) {
        throw "传参错误"
    }
    let { form = false } = config;
    log("url:", SCHEME + GATEWAY_HOST + '/' + server + (path.startsWith('/') ? path : ('/' + path)))
    const axiosConfig = {
        method,
        params: method == 'get' && data ? data : {},
        url: SCHEME + GATEWAY_HOST + '/' + server + (path.startsWith('/') ? path : ('/' + path)),
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

function parseAvatarToSrc(path) {
    if (!path || path == '')
        return defalutAvatarImage
    return SCHEME + GATEWAY_HOST + '/' + "store/direct" + (path.startsWith('/') ? path : ('/' + path))
}


export { request, parseAvatarToSrc }