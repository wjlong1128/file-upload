// 通用返回值类型 {message:'',code:200,data:<T>}
const request = (url, method, obj) => {
    return new Promise((resolve, reject) => {
        try {
            method = method.toUpperCase()
            if (method == 'GET') {
                if (obj) {
                    url += '?'
                    for (key in obj) {
                        let value = obj[key]
                        /*if (Array.isArray(value)) {
                            value = value.toString()
                        }*/
                        url += `${key}=${value}&`
                    }
                    url = url.substring(0, url.length - 1)
                }
                fetch(url, {
                    method: 'GET',
                    headers: {
                        "Content-Type": "application/json",
                        // 'Content-Type': 'application/x-www-form-urlencoded',
                    }
                }).then(res => {
                    res.json().then(json => {
                        if (json && json.code == 200) {
                            resolve(json.data)
                        } else {
                            reject(json?.message || '请求出错')
                        }
                    })
                })
            } else {
                fetch(url, {
                    method,
                    headers: {
                        "Content-Type": "application/json",
                        // 'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: JSON.stringify(obj || {})
                })
                    .then(res => {
                        res.json().then(json => {
                            if (json && json.code == 200) {
                                resolve(json.data)
                            } else {
                                reject(json.message || '请求出错')
                            }
                        })
                    })
            }
        } catch (e) {
            reject(e)
        }
    })
}


const get = (url, obj) => {
    return request(url, 'GET', obj);
}


const post = (url, obj) => {
    return request(url, 'POST', obj)
}


const deleteRequest = (url, obj) => {
    return request(url, 'DELETE', obj)
}

const put = (url, obj) => {
    return request(url, 'PUT', obj)
}


const upload = (url, fromData) => {
    return new Promise((resolve, reject) => {
        try {
            fetch(url, {
                method: 'POST',
                headers: {
                    // 'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: fromData
            })
                .then(res => {
                    res.json().then(json => {
                        if (json && json.code == 200) {
                            resolve(json.data)
                        } else {
                            reject(json.message || '请求出错')
                        }
                    })
                })
        } catch (e) {
            reject(e)
        }
    })
}


