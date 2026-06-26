import xuanbi from '../static/img/xb.png'
import xuandian from '../static/img/xd.png'

export const fieldMap = {
    "nickname": { display: "昵称", required: true },
    "realName": { display: "真实姓名", required: false },
    "gender": { display: "性别", required: true },
    "region": { display: "地区", required: true },
    'email': { display: "电子邮件", required: false },
    "password": { display: "密码", required: true },
    "phone": { display: "手机号", required: true },
    "signature": { display: "个性签名" },
    "age": { display: "年龄" },
    "idNumber": { display: "身份证号" },
    "description": { display: "个人简介" }
}

const currenciesMap = new Map()
currenciesMap.set(1, { currencyId: 1, icon: xuanbi, name: '炫币' })
currenciesMap.set(2, { currencyId: 2, icon: xuandian, name: '炫点' })


const giftMap = new Map();
giftMap.set(1, { id: 1, name: "火箭", src: "https://huyaimg.msstatic.com/cdnimage/actprop/20269_1__45_1603683936.jpg", price: "80炫币", description: "火箭" })
giftMap.set(2, { id: 2, name: "跑车", src: "https://huyaimg.msstatic.com/cdnimage/actprop/20493_1__45_1611656794.jpg", price: "1炫币50炫点", description: "精致的小车" })
giftMap.set(3, { id: 3, name: "爱心", src: "https://huyaimg.msstatic.com/cdnimage/actprop/20201_1__45_1551436623.jpg", price: "50炫点", description: "送给主播小爱心" })

const stateMap = new Map();
stateMap.set("熊出没", "点播")

export { giftMap, stateMap, currenciesMap } 