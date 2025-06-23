import axios from "axios";
import { useUserStore } from '@/stores/user'
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const baseURL = "http://localhost:9999";
export function reqeust(config) {
  const router = useRouter()
  const instance = axios.create({
    baseURL,
    timeout: 5000,
  });
  // 请求拦截
  instance.interceptors.request.use(
    // 封装token
    (config) => {
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = `${userStore.token}`;
      }
      return config;
    },
    (err) => {
      return Promise.reject(err);
    }
  );
  // 响应拦截器
    instance.interceptors.response.use(
        (res) => {
            // 确保返回完整的响应数据
            return res.data;  // 修改这里，直接返回res.data
        },
        (err) => {
            ElMessage.error(err.response?.data?.msg || err.message || '请求失败');
            return Promise.reject(err);
        }
  );


  return instance(config);
}

export function get(url, params) {
  return reqeust({
    url,
    method: "get",
    params,
  });
}
export function post(url, data) {
  return reqeust({
    url,
    method: "post",
    data:JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    }
  });
}
export function del(url, params) {
    return reqeust({
        url,
        method: "delete",
        params,
        headers: {
            'Content-Type': 'application/json'
        }
    });
}
export function put(url, data) {  // 参数名改为data
    return reqeust({
        url,
        method: "put",
        data: JSON.stringify(data), // 使用传入的data参数
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

export { baseURL };