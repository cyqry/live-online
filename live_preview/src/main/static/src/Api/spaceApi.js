import { log } from "../Common/util";
import { request } from "./api"


async function getRoomItems(secondId) {
    return (await spaceRequest('get', "/public/getRoomItems", { secondId })).data
}

async function getAllCategories() {
    let res = await spaceRequest('get', "/public/getAllCategories", {})
    return res.data
}
async function getRoomTag(roomId) {
    let res = await spaceRequest("get", "/getRoomTag", { roomId });
    return res.data;
}

/**
 * roomId可以不传
 */
async function getRoomInfo(roomId) {
    let res = await spaceRequest("get", "/getRoomInfo", roomId ? { roomId } : {});
    return res.data;
}

//房间在线才能查出
async function getRoomPublic(roomId) {
    let res = await spaceRequest("get", "/public/getRoomPublic", roomId ? { roomId } : {});
    return res.data;
}

async function createRoom(data) {
    return await spaceRequest("post", '/createRoom', data, { form: true });

}
async function getRoomBase(roomId) {
    return (await spaceRequest('get', '/public/getRoomBase', { roomId })).data

}

async function subscribe(roomId) {
    return await spaceRequest('post', "/subscribe", { roomId }, { form: true })
}
async function unsubscribe(roomId) {
    return await spaceRequest('post', "/unsubscribe", { roomId }, { form: true })
}

async function report({ roomId, reason, detail, screenshotBase64s, contactWay }) {
    return await spaceRequest('post', "/report", { roomId, reason, detail, screenshotBase64s, contactWay })
}

async function getSecondLevels() {
    return (await (spaceRequest('get', "/public/getSecondLevels"))).data
}

async function getUserHistory() {
    return (await spaceRequest('get', "/getUserHistory")).data
}

async function getFollow(roomId) {
    return (await spaceRequest('get', "/getFollow", { roomId })).data
}
async function bannedLive(roomId) {
    return await spaceRequest('post', "/bannedLive", { roomId }, { form: true })
}

async function isRoomAdmin(roomId) {
    return (await spaceRequest('get', "/isRoomAdmin", { roomId }, { form: true })).data
}

async function getAttentionCount(roomId) {
    return (await spaceRequest('get', "/getAttentionCount", { roomId })).data
}

async function getReports() {
    return (await spaceRequest('get', "/getReports")).data
}

async function getFollowInfo(pageSize) {
    return (await spaceRequest('get', "/getFollowInfo", { pageSize })).data
}

async function getOperations(type) {
    return (await spaceRequest('get', "/getOperations", { type })).data;
}

async function takeOfficeList() {
    return (await spaceRequest('get', "/takeOfficeList")).data;
}

async function removeOperating(recordId) {
    return (await spaceRequest('post', "/removeOperating", { recordId }, { form: true })).data;
}

async function searchRelativeRoomBase(condition) {
    return (await spaceRequest('get', "/public/searchRelativeRoomBase", { condition })).data
}

async function selectSecondRoom(secondId) {
    return (await spaceRequest("get", "/public/selectSecondRoom", { secondId })).data
}

async function removeReport(roomId) {
    return await spaceRequest("post", "/removeReport", { roomId }, { form: true })
}

async function getRoomSearchsByName(secondLevelName, roomItemName) {
    return (await spaceRequest('get', "/public/getRoomSearchsByName", { secondLevelName, roomItemName })).data

}

async function spaceRequest(method, path, data, config = {}) {
    log(method, path, data, config)
    return await request('space', method, path, data, config)
}





export { getRoomSearchsByName, getAttentionCount, getSecondLevels, selectSecondRoom, getRoomItems, searchRelativeRoomBase, removeOperating, takeOfficeList, getOperations, getFollowInfo, report, removeReport, getReports, isRoomAdmin, bannedLive, getAllCategories, getRoomInfo, getRoomTag, createRoom, getUserHistory, getRoomBase, getRoomPublic, unsubscribe, subscribe, getFollow }