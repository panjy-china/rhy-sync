import {get,post} from './request'

// 获取视频列表
export const getVideoList = (params) => {
    return get('/vedio/page',params)
}

// 获取视频详情
export const getVideoDetail = (id) => {
    return get('/vedio/detail',{id})
}
