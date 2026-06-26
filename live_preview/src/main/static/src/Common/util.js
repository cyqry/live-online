
import defaultAvatar from '../static/img/OIP.jpg'
import logoImage from '../static/img/logo.png'
import headerBackImage from '../static/img/hero-bg.webp'
import { Model } from '../Config/config';

let widthTemp = [];
let heightTemp = [];

for (let i = 0; i <= 100; i++) {
  widthTemp.push(width(i))
  heightTemp.push(height(i))
}

function height(x) {
  if (x.toString().endsWith("px")) {
    return Number(x.substring(0, x.length - 2))
  }

  if (x < 0 || x > 250) {
    log(x)
    throw new Error("？");
  } else if (Math.floor(x) === x && typeof heightTemp[Math.floor(x)]
    !== 'undefined') {
    return heightTemp[Math.floor(x)];
  }
  let h = document.documentElement.clientHeight || document.body.clientHeight;
  return ((h * x) / 100) + "px";
}

function width(x) {
  if (typeof x === "string" && x.endsWith("px")) {
    return Number(x.substring(0, x.length - 2))
  }
  if (x < 0 || x > 250) {
    log(x)
    throw new Error("？");
  } else if (Math.floor(x) === x && typeof widthTemp[Math.floor(x)]
    !== 'undefined') {
    return widthTemp[Math.floor(x)];
  }
  let w = document.documentElement.clientWidth || document.body.clientWidth;
  return ((w * x) / 100) + "px";
}

function addAndGetLength(w, n) {
  if (typeof w === 'undefined' || typeof n === 'undefined') {
    throw new Error("?")
  } else if (!w.toString().endsWith("px")) {
    throw new Error("?")
  } else {
    let number1 = Number(w.toString().substring(0, w.toString().length - 2));
    if (isNaN(number1)) {
      throw new Error("?")
    }
    let number2 = n.toString().endsWith("px")
      ? Number(n.toString().substring(0, n.toString().length - 2))
      : Number(n.toString())
    if (isNaN(number2)) {
      throw new Error("?")
    }

    return number1 + number2 + "px"
  }
}

async function getMediaStream(choose, deviceIndex) {
  let result;
  let userMediaConstraints = isMobile() ? { audio: true, video: { width: '1059px', height: '595.5px', facingMode: "user" } }
    : { audio: true, video: { width: '1059px', height: '595.5px' } };
  let displayMediaConstraints = isMobile() ? {}
    : { audio: false, video: true };

  //摄像头
  if (choose == 1) {
    result = await navigator.mediaDevices.getUserMedia(userMediaConstraints);
  } else if (choose == 0) {
    result = await navigator.mediaDevices.getDisplayMedia(displayMediaConstraints);
    log("得到屏幕流")
  } else {
    throw new Error("?")
  }
  return result;

}

function isMobile() {
  return !!window.navigator.userAgent.match(
    /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);
}
function logo() {
  return logoImage
}

function multiplyAndGetLength(w, n) {
  if (typeof w === 'undefined' || typeof n === 'undefined') {
    throw new Error("?")
  } else if (!w.toString().endsWith("px")) {
    throw new Error("?")
  } else {
    let number1 = Number(w.toString().substring(0, w.toString().length - 2));
    if (isNaN(number1)) {
      throw new Error("?")
    }
    let number2 = Number(n)
    if (isNaN(number2)) {
      throw new Error("?")
    }

    return number1 * number2 + "px"
  }
}

function headerBack() {
  return headerBackImage
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}

function filterBlank(obj) {
  try {
    Object.keys(obj).forEach((key) => {
      if (typeof (obj[key]) === 'undefined' || obj[key] === null || obj[key] === '') {
        delete obj[key];
      }
    });
    return obj;
  } catch (e) {
    log(e)
    return null
  }
}
function base64ToSrcOrDefault(base64) {
  return base64 ? ("data:image/png;base64," + base64) : defaultAvatar;
}

function createObjectForForm(form) {
  let obj = {};

  let formData = new FormData(form)
  // 遍历 formData 的 entries
  for (let [key, value] of formData.entries()) {
    obj[key] = value;
  }

  return obj;
}

function getRandomLong() {
  const min = Number.MAX_SAFE_INTEGER / 100000;
  const max = Number.MAX_SAFE_INTEGER;

  return Math.floor(Math.random() * (max - min + 1) + min);
}

function getDataFromErrorOrDefault(e, defaultData) {
  if (!defaultData || defaultData == '') {
    defaultData = "网络错误"
  }

  log(e)
  if (e && e.response && e.response.data && e.response.data != '' && e.response.data.error == undefined && !e.response.status.toString().startsWith("5")) {
    return e.response.data
  } else {
    return defaultData
  }
}

function convertDateTimeToDate(dateTime) {
  var date = new Date(dateTime); // 创建一个新的Date对象

  var year = date.getFullYear(); // 获取年份
  var month = ('0' + (date.getMonth() + 1)).slice(-2); // 获取月份，并补零
  var day = ('0' + date.getDate()).slice(-2); // 获取日期，并补零

  var formattedDate = year + '-' + month + '-' + day; // 格式化日期字符串
  return formattedDate;
}

function log(...args) {
  if (Model == "debug") {
    console.log('[DEBUG]', ...args);
    try {
      throw new Error();
    } catch (error) {
      const stackTrace = error.stack.split('\n');
      // 跳过第一行，获取调用 log 方法的位置
      const caller = stackTrace[2];
      console.log('[DEBUG] Called from:', caller);
    }
  }
}

function calculateTimeDifference(startDateTime) {

  var startDate = new Date(startDateTime); // 创建开始日期的Date对象
  var endDate = new Date(); // 创建当前日期的Date对象

  var timeDifference = endDate.getTime() - startDate.getTime(); // 计算时间差（以毫秒为单位）

  if (timeDifference < 60000) { // 少于1分钟
    return '刚刚';
  } else if (timeDifference < 3600000) { // 少于1小时
    var minutes = Math.floor(timeDifference / 60000); // 将时间差转换为分钟
    return minutes + '分钟前';
  } else if (timeDifference < 86400000) { // 少于一天
    var hours = Math.floor(timeDifference / 3600000); // 将时间差转换为小时
    return hours + '小时前';
  } else if (timeDifference < 2592000000) { // 少于一个月
    var days = Math.floor(timeDifference / 86400000); // 将时间差转换为天
    return days + '天前';
  } else if (timeDifference < 31536000000) { // 少于一年
    var months = Math.floor(timeDifference / 2592000000); // 将时间差转换为月
    return months + '个月前';
  } else { // 一年及以上
    var years = Math.floor(timeDifference / 31536000000); // 将时间差转换为年
    return years + '年前';
  }
}





export {
  log, calculateTimeDifference, convertDateTimeToDate, getDataFromErrorOrDefault, headerBack, logo, height, width, addAndGetLength, randomInt, multiplyAndGetLength, getMediaStream, isMobile, base64ToSrcOrDefault, getRandomLong, createObjectForForm, filterBlank,
}