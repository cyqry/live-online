const form = document.getElementById("request-form");
const responseElement = document.getElementById("response");


form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const method = document.getElementById("method").value;
    const url = document.getElementById("url").value;
    const data = document.getElementById("data").value;
    const dataFormat = document.getElementById("data-format").value;


    try {

        const axiosConfig = {
            method,
            url,
            withCredentials: true,
            headers: {
                "Content-Type":
                    "application/json"
            }
        };

        responseElement.textContent = "发送中...";

        if (method === "post" && data) {
            if (dataFormat === "json") {
                axiosConfig.headers["Content-Type"] = "application/json";
                try {
                    axiosConfig.data = JSON.parse(data);
                } catch (error) {
                    responseElement.textContent = "无效的 JSON 数据";
                    return;
                }
            } else if (dataFormat === "form-data") {

                axiosConfig.headers["Content-Type"] = "application/x-www-form-urlencoded";
                axiosConfig.data = new URLSearchParams(data.replace(/(?:\r\n|\r|\n)/g, '&'));
            }
        }

        const response = await axios(axiosConfig);

        responseElement.textContent = JSON.stringify(response.data, null, 2);
    } catch (error) {
        if (error.response) {
            // 请求已发出，但服务器响应的状态码不在 2xx 范围内
            responseElement.textContent = `错误：${error.response.status}\n${error.response.data}`;
        } else if (error.request) {
            // 请求已发出，但未收到响应
            responseElement.textContent = "请求未收到响应";
        } else {
            // 发送请求时出现错误
            responseElement.textContent = "请求发送失败";
        }
    }
})
