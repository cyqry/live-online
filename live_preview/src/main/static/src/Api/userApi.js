
import { log } from "../Common/util";
import { request } from "./api";

async function attention(id, roomId, follow) {
    return await userRequest('post', "/attention", {
        id,
        roomId,
        //为true就取关，否则关注
        action: follow ? 0 : 1,
    });
}


async function getAnchor() {
    return (await userRequest('get', '/getAnchor')).data;
}

async function registerAnchor(data) {
    return await userRequest('post', "/registerAnchor", data, { form: true });
}

async function signUp(user) {
    return await userRequest('post', "/signUp", user)
}

async function signIn(formdata) {
    return await userRequest('post', "/signIn", formdata, { form: true })
}
async function getUser() {
    let res = await userRequest('get', "/getUser", {});
    return res.data;
}

async function recharge(property) {
    return await userRequest('post', "/recharge", property);
}

async function updateUser(user) {
    let res = await userRequest('post', "/updateUser", user);
    return res.data;
}

async function getWholeUser() {
    let res = await userRequest('get', "/getWholeUser", {});
    return res.data;
}

async function getIsAnchor() {
    return (await userRequest('get', "/isAnchor")).data
}

async function getRoomAdmins() {
    return (await userRequest("get", "/getRoomAdmins")).data
}

async function deleteRoomAdmin(personalityId) {
    return (await userRequest('post', "/deleteRoomAdmin", { personalityId }, { form: true }))
}

async function getAnchorPublic(roomId) {
    return (await userRequest('get', "/public/getAnchorPublic", { roomId })).data
}

async function getUserByPersonalityId(personalityId) {
    return (await userRequest('get', "/getUserByPersonalityId", { personalityId })).data
}

async function sendGift(roomId, gifts) {
    return await userRequest('post', "/sendGift", { roomId, gifts })
}

async function userRequest(method, path, data, config = {}) {
    return await request('account', method, path, data, config)
}

async function searchRelativeAnchor(condition) {
    return (await userRequest('get', "/public/searchRelativeAnchor", { condition })).data
}

async function logout() {
    return await userRequest('post', "/logout")
}
async function addRoomAdmin(personalityId) {
    return (await userRequest("post", "/addRoomAdmin", { personalityId }, { form: true }))
}
async function identity(idNumber, realName) {
    return await userRequest('post', "/identity", { idNumber, realName }, { form: true })
}

export { identity, deleteRoomAdmin, addRoomAdmin, getUserByPersonalityId, getRoomAdmins, getIsAnchor, searchRelativeAnchor, logout, attention, signIn, signUp, getUser, updateUser, registerAnchor, getAnchor, getCookieValue, getWholeUser, recharge, sendGift, getAnchorPublic }