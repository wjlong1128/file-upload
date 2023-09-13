const GET = 'GET';
const POST = 'POST';
const DELETE = 'DELETE';
const PUT = 'PUT'


function getUrlConvert(url, data = {}) {
    url += '?';
    for (let key in data) {
        let value = data[key];
        url += `${key}=${value}&`;
    }
    return url.substring(0, url.length - 1);
}

function request(options) {
    if (!options.method || !options.url) {
        throw Error('参数异常')
    }
    this.baseURL = options.baseURL;
    if (this.baseURL) {
        this.url = this.baseURL + options.url;
    } else {
        this.url = options.url;
    }
    this.method = options.method.toUpperCase();
    this.headers = options.headers || {'Content-Type': 'application/json'};
    this.data = options.data || {};
    this.timeout = options.timeout || 20000;
    return new Promise(async (resolve, reject) => {
        try {
            let request_url = this.url;
            let request_data = this.data
            const fetch_options = {
                method: this.method
            };
            // 对请求方法的特殊处理
            if (this.method === GET && request_data && Object.keys(request_data).length > 0) {
                request_url = getUrlConvert(request_url, request_data)
                fetch_options.body = null
            } else if (this.method === POST || this.method === PUT) {
                let data_type = Object.prototype.toString.call(this.data);
                if (data_type === '[object File]' || data_type === '[object Blob]') {
                    // 判断为文件类型
                    this.headers['Content-Type'] = null;
                } else {
                    fetch_options.body = JSON.stringify(request_data);
                }
            }
            fetch_options.headers = this.headers;
            let controller = new AbortController();
            const timeout_id = setTimeout(() => {
                controller.abort()
            }, this.timeout);
            const response = await fetch(request_url, {
                ...fetch_options,
                // 超时处理
                signal: controller.signal
            });
            clearTimeout(timeout_id);
            const json = await response.json();
            resolve(json);
        } catch (e) {
            reject(e);
        }
    })
}

request.get = function (url, data, headers, timeout) {
    return request({method: GET, url, data, headers, timeout})
}

request.post = function (url, data, headers, timeout) {
    return request({method: POST, url, data, headers, timeout})
}



