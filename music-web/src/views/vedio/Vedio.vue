<script setup>
import {ref} from 'vue'
import {baseURL} from '@/api/request'
import {formatDate} from '@/utils/format'
import {getVideoList, getVideoDetail} from '@/api/vedio.js'

const videos = ref([])
const queryParams = ref({
  pageNo: 1,
  size: 7,
  search: ''
})
const total = ref(0)

// 获取视频列表
const fetchVideos = async () => {
  const {data} = await getVideoList(queryParams.value)
  console.log(data)
  videos.value = data.records
  total.value = data.total
}
fetchVideos()

// 分页
const handlePageChange = (pageNo, size) => {
  queryParams.value.pageNo = pageNo
  queryParams.value.size = size
  fetchVideos()
}

// 搜索
const handleSearch = () => {
  queryParams.value.pageNo = 1 // 重置页码
  fetchVideos()
}

// 视频播放列表
const videoQueue = ref([])

// 播放视频
const playVideo = async (row) => {
  const {data} = await getVideoDetail(row.id)
  data.vUrl = baseURL + data.vUrl
  data.vImg = baseURL + data.vImg // 视频封面
  videoQueue.value.unshift(data) // 添加到播放队列
}

// 编辑视频
const editVideo = (row) => {
  // 编辑逻辑...
}

// 删除视频
const deleteVideo = (row) => {
  ElMessageBox.confirm('确定要删除此视频吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 调用删除接口...
    ElMessage.success('删除成功')
    fetchVideos() // 刷新列表
  })
}
</script>

<template>
  <el-card style="max-width: 100%">
    <template #header>
      <div class="card-header">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="视频名称">
            <el-input
                placeholder="请输入视频名称"
                v-model="queryParams.search"
                clearable
                @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </template>

    <el-table :data="videos" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column label="视频名称">
        <template #default="{ row }">
          <el-link type="primary" @click="playVideo(row)">{{ row.vname }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="作者">
        <template #default="{ row }">
          {{ row.vauthor }}
        </template>
      </el-table-column>
      <el-table-column label="播放量" prop="vplayback" width="100"/>
      <el-table-column label="上传时间" prop="vcreatetime" :formatter="formatDate" width="180"/>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="editVideo(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteVideo(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="queryParams.size"
          @current-change="handlePageChange"
      />
    </div>
  </el-card>

  <!-- 视频播放器组件 -->
  <VideoPlayer
      :videos="videoQueue"
      :auto-play="true"
      style="margin-top: 20px"
  />
</template>

<style scoped>
.el-card {
  min-height: 100%;
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>