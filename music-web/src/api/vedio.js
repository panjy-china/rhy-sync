// src/api/vedio.js
import { get, del, put } from './request'  // 确保导入路径正确

export const getVideoList = (params) => {
    return get('/vedio/page', params)
}

export const getVideoDetail = (id) => {
    return get('/vedio/detail', { id })
}

export const deleteVideoById = (id) => {
    console.log('deleteVideoById', id)
    return del('/vedio/del', { id })  // 使用 del 方法并传递 id 参数
}

export const updateVideo = (data) => {
    return put('/vedio/update', data)
}