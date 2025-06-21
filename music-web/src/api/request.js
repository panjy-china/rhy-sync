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
      if (res.data.code == 0) {
        return res.data;
      }
      // 业务请求异常
      ElMessage.error(res.data.msg ?? '服务异常');
      if (res.data.code == 1008) {
        const userStore = useUserStore()
        userStore.setToken(null)
        router.push({ name: 'login' })
      }
    },
    (err) => {
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

export { baseURL };