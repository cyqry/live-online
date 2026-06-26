import { log } from "../Common/util"
import { request } from "./api"


async function getImage(path) {
    let res = await storeRequest('get', path)
    return res.data
}


async function storeRequest(method, path, data, config = {}) {
    log(method,path,data,config)
    return await request('store', method, path, data, config)
}
export { getImage }