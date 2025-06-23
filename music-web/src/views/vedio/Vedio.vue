<script setup>
import { ref } from 'vue'
import { baseURL } from '@/api/request'
import { formatDate } from '@/utils/format'
import {getVideoList, getVideoDetail, deleteVideoById, updateVideo} from '@/api/vedio.js'
import VideoPlayer from '@/components/VedioPlayer.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const videos = ref([])
const queryParams = ref({
  pageNo: 1,
  size: 10,
  search: ''
})
const total = ref(0)

// 弹窗控制状态
const showVideoDialog = ref(false)
const currentVideoData = ref(null)
const loading = ref(false)

const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  vname: ''
})

// 编辑视频
const editVideo = (row) => {
  editForm.value = {
    id: row.id,
    vname: row.vname
  }
  editDialogVisible.value = true
}

// 提交修改
const submitEdit = async () => {
  try {
    if (!editForm.value.vname?.trim()) {
      ElMessage.warning('视频名称不能为空');
      return;
    }

    const response = await updateVideo({
      id: editForm.value.id,
      vname: editForm.value.vname
    });

    console.log('修改响应:', response);

    await fetchVideos()
    editDialogVisible.value = false;;
    if (response?.code === 200) {
      ElMessage.success(response.msg || '修改成功');
    } else {
      ElMessage.error(response?.msg || '修改失败');
    }
  } catch (error) {
    console.error('修改操作失败:', error);
    ElMessage.error(error.response?.data?.msg || error.message || '修改操作失败');
  }
}

// 获取视频列表
const fetchVideos = async () => {
  try {
    console.log(queryParams)
    const { data } = await getVideoList(queryParams.value)
    videos.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('获取视频列表失败')
  }
}

fetchVideos()

// 分页
const handlePageChange = (pageNo) => {
  queryParams.value.pageNo = pageNo
  fetchVideos()
}

// 搜索
const handleSearch = () => {
  queryParams.value.pageNo = 1
  fetchVideos()
}

// 播放视频
const playVideo = async (row) => {
  try {
    loading.value = true
    const { data } = await getVideoDetail(row.id)

    if (!data.vurl) {
      throw new Error('无效的视频URL')
    }

    currentVideoData.value = {
      vUrl: baseURL + data.vurl,
      vImg: data.vimg || '',
      vName: row.vname || '未命名视频'
    }
    showVideoDialog.value = true
  } catch (error) {
    ElMessage.error('加载视频失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 删除视频
const deleteVideo = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此视频吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 关键修改：添加await并正确处理响应
    const response = await deleteVideoById(row.id);
    console.log('删除响应:', response); // 调试用
    await fetchVideos();
    // 根据你的ResultUtil结构判断
    if (response && response.code === 200) {
      ElMessage.success(response.msg || '删除成功');
    } else {
      ElMessage.error(response?.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除错误详情:', error); // 调试用
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || error.message || '删除操作失败');
    }
  }
}
</script>

<template>
  <!-- 在template中添加编辑对话框 -->
  <el-dialog
      v-model="editDialogVisible"
      title="编辑视频信息"
      width="500px"
  >
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="视频名称">
        <el-input v-model="editForm.vname" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">确定</el-button>
    </template>
  </el-dialog>
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

    <el-table :data="videos" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
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
      <el-table-column label="播放量" prop="vplayback" width="100" />
      <el-table-column label="上传时间" prop="vcreatetime" :formatter="formatDate" width="180" />
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

    <!-- 视频播放弹窗 -->
    <el-dialog
        v-model="showVideoDialog"
        :title="currentVideoData?.vName || '视频播放'"
        width="80%"
        top="5vh"
        :close-on-click-modal="false"
        @closed="currentVideoData = null"
    >
      <VideoPlayer
          v-if="showVideoDialog && currentVideoData"
          :videos="[currentVideoData]"
          :auto-play="true"
          style="height: 60vh"
      />
      <template #footer>
        <el-button @click="showVideoDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
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