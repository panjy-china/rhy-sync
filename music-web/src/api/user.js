import {get,post} from './request'
// 登录
export function login(params){
  return post('/admin/login',params)
}