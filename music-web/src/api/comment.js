import {get,post,reqeust} from './request'
import {formatDate3} from '../utils/format'

// 获取评论列表
export function getCommentList(params) {
  return get('/comment/page',params)
}

// 根据用户Id 获取评论列表
export function getCommentListByUserId(params) {
  return get('/comment/pagebyuserid',params)
}

// 获取评论详情
export function getCommentDetail(id) {
  return get('/comment/detail',{id})
}

// 添加或修改评论
export function addOrUpdateComment(data){
  if (data.birth instanceof Date) {
    data.birth = formatDate3(data.createdTime);
  }
  return post('/comment/add',data)
}

// 根据点赞数从高到低返回分页数据
export function getCommentListByUp(params) {
    return get('/comment/pagebyup',params)
}

// 删除评论
export function deleteComment(id) {
  return get('/comment/delete',{id})
}