import axios from "axios";
import {ElMessage} from "element-plus";

// 默认实现
const defaultError = () => ElMessage.error('系统繁忙，请稍后重试')
const defaultFailure = (data) => ElMessage.warning(data)

// POST请求
function post(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true
    }).then(({data}) => {
        if (data.success) {
            success(data.data, data.code)
        } else {
            failure(data.data, data.code)
        }
    }).catch(error)
}

// GET请求
function get(url, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {
        withCredentials: true
    }).then(({data}) => {
        if (data.success) {
            success(data.data, data.code)
        } else {
            failure(data.data, data.code)
        }
    }).catch(error)
}

// 暴露方法
export {get, post}