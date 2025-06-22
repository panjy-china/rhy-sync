<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/format'

// 导入API函数
import { 
  getCommentList, 
  getCommentListByUserId as fetchCommentListByUserId,
  deleteComment as deleteCommentApi,
  getCommentDetail,
  addOrUpdateComment,
  getCommentListByUp
} from '@/api/comment'

const comments = ref([])
const total = ref(0)
const loading = ref(false)
const queryParams = ref({
  pageNo: 1,
  size: 7,
  search: ''
})
const sortMode = ref('default') // 排序模式：default（默认）| up（按点赞数）

// 对话框相关状态
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const addDialogVisible = ref(false) // 新增评论对话框
const currentComment = ref({
  id: null,
  userId: null,
  type: 0,
  songId: null,
  songListId: null,
  content: '',
  createTime: null,
  up: 0
}) 

// 新评论表单
const newComment = ref({
  userId: null,
  type: 0,
  songId: null,
  songListId: null,
  content: '',
  up: 0
})

// 评论类型选项
const commentTypes = ref([
  { value: 0, label: '歌曲评论' },
  { value: 1, label: '歌单评论' }
])

// 加载评论数据
const loadComments = async () => {
  loading.value = true
  try {
    let res
    
    // 根据当前排序模式调用不同的API
    if (sortMode.value === 'up') {
      // 按点赞数排序模式
      res = await getCommentListByUp({
        pageNo: queryParams.value.pageNo,
        size: queryParams.value.size
      })
    } else if (queryParams.value.search) {
      // 搜索模式 - 使用用户ID搜索
      res = await fetchCommentListByUserId({
        search: queryParams.value.search,
        pageNo: queryParams.value.pageNo,
        size: queryParams.value.size
      })
    } else {
      // 普通列表模式
      res = await getCommentList({
        pageNo: queryParams.value.pageNo,
        size: queryParams.value.size
      })
    }
    
    if (res && res.data) {
      comments.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.warning('未获取到数据')
      comments.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载评论数据失败:', error)
    ElMessage.error('加载评论数据失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 初始化加载
onMounted(loadComments)

// 分页处理
const handlePaginationChange = (pageNo, size) => {
  queryParams.value.pageNo = pageNo
  queryParams.value.size = size
  loadComments()
}

// 搜索处理
const handleSearch = () => {
  if (queryParams.value.search && !/^\d+$/.test(queryParams.value.search)) {
    ElMessage.warning('用户ID必须是数字')
    return
  }
  sortMode.value = 'default' // 重置排序模式为默认
  queryParams.value.pageNo = 1
  loadComments()
}

// 重置搜索
const resetSearch = () => {
  queryParams.value.search = ''
  sortMode.value = 'default' // 重置排序模式为默认
  queryParams.value.pageNo = 1
  loadComments()
}

// 按点赞数排序
const sortByUp = () => {
  sortMode.value = 'up' // 设置排序模式为按点赞数
  queryParams.value.search = '' // 清空搜索条件
  queryParams.value.pageNo = 1 // 重置到第一页
  loadComments()
}

// 打开新增评论对话框
const openAddDialog = () => {
  // 重置新评论表单
  newComment.value = {
    userId: null,
    type: 0,
    songId: null,
    songListId: null,
    content: '',
    up: 0
  }
  addDialogVisible.value = true
}

// 提交新增评论
const submitAdd = async () => {
  try {
    // 验证必要字段
    if (!newComment.value.userId) {
      ElMessage.warning('用户ID不能为空')
      return
    }
    
    if (!newComment.value.content.trim()) {
      ElMessage.warning('评论内容不能为空')
      return
    }
    
    // 根据类型验证相关ID
    if (newComment.value.type === 0 && !newComment.value.songId) {
      ElMessage.warning('歌曲评论必须关联歌曲ID')
      return
    }
    
    if (newComment.value.type === 1 && !newComment.value.songListId) {
      ElMessage.warning('歌单评论必须关联歌单ID')
      return
    }
    
    // 调用添加评论接口
    const res = await addOrUpdateComment(newComment.value)
    if (res && res.code === 200) {
      ElMessage.success('评论添加成功')
      addDialogVisible.value = false
      // 重置到第一页并刷新列表
      queryParams.value.pageNo = 1
      loadComments()
    } else {
      ElMessage.error(res?.message || '评论添加失败')
    }
  } catch (error) {
    console.error('评论添加失败:', error)
    ElMessage.error('评论添加失败: ' + (error.response?.data?.message || error.message))
  }
}

// 查看评论详情
const showCommentDetail = async (row) => {
  try {
    const res = await getCommentDetail(row.id)
    if (res && res.data) {
      currentComment.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.warning('获取评论详情失败')
    }
  } catch (error) {
    console.error('获取评论详情失败:', error)
    ElMessage.error('获取评论详情失败: ' + (error.response?.data?.message || error.message))
  }
}

// 编辑评论 - 初始化表单数据
const editComment = (row) => {
  // 确保所有字段都有值，避免undefined
  currentComment.value = {
    id: row.id,
    userId: row.userId,
    type: row.type || 0,
    songId: row.songId || null,
    songListId: row.songListId || null,
    content: row.content || '',
    createTime: row.createTime,
    up: row.up || 0
  }
  editDialogVisible.value = true
}

// 提交评论编辑
const submitEdit = async () => {
  try {
    // 验证必要字段
    if (!currentComment.value.content.trim()) {
      ElMessage.warning('评论内容不能为空')
      return
    }
    
    // 根据类型验证相关ID
    if (currentComment.value.type === 0 && !currentComment.value.songId) {
      ElMessage.warning('歌曲评论必须关联歌曲ID')
      return
    }
    
    if (currentComment.value.type === 1 && !currentComment.value.songListId) {
      ElMessage.warning('歌单评论必须关联歌单ID')
      return
    }
    
    // 调用添加/更新评论接口
    const res = await addOrUpdateComment(currentComment.value)
    if (res && res.code === 200) {
      ElMessage.success('评论更新成功')
      editDialogVisible.value = false
      loadComments()
    } else {
      ElMessage.error(res?.message || '评论更新失败')
    }
  } catch (error) {
    console.error('评论更新失败:', error)
    ElMessage.error('评论更新失败: ' + (error.response?.data?.message || error.message))
  }
}

// 删除评论
const handleDelete = (row) => { 
  ElMessageBox.confirm(`确定要删除ID为 ${row.id} 的评论吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => { 
    try {
      await deleteCommentApi(row.id)
      ElMessage.success('删除成功')
      loadComments()
    } catch (error) {
      console.error('删除失败:', error)
      let errorMessage = '删除失败'
      
      // 更详细的错误处理
      if (error.response) {
        if (error.response.status === 404) {
          errorMessage = '删除失败: 接口未找到 (404)'
        } else if (error.response.data && error.response.data.message) {
          errorMessage = `删除失败: ${error.response.data.message}`
        } else {
          errorMessage = `删除失败: HTTP ${error.response.status}`
        }
      } else if (error.request) {
        errorMessage = '删除失败: 无服务器响应'
      }
      
      ElMessage.error(errorMessage)
    }
  })
}
</script>

<template>
  <el-card style="max-width: 100%">
    <!-- 搜索框和操作按钮 -->
    <template #header>
      <div class="card-header">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="用户ID">
            <el-input 
              placeholder="请输入用户ID" 
              clearable 
              v-model="queryParams.search" 
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
          
          <!-- 按点赞数排序按钮 -->
          <el-form-item>
            <el-button 
              type="success" 
              @click="sortByUp"
              :plain="sortMode !== 'up'"
            >
              按点赞数排序
              <el-icon v-if="sortMode === 'up'"><Check /></el-icon>
            </el-button>
          </el-form-item>
          
          <!-- 新增评论按钮 -->
          <el-form-item>
            <el-button 
              type="primary" 
              @click="openAddDialog"
              icon="Plus"
            >
              新增评论
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </template>

    <!-- 评论表格 -->
    <el-table 
      :data="comments" 
      style="width: 100%" 
      v-loading="loading"
      element-loading-text="加载中..."
      element-loading-background="rgba(255, 255, 255, 0.8)"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="用户ID" width="120">
        <template #default="{ row }">
          <el-link type="primary">{{ row.userId }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          {{ row.type === 0 ? '歌曲评论' : '歌单评论' }}
        </template>
      </el-table-column>
      <el-table-column label="关联ID" width="120">
        <template #default="{ row }">
          {{ row.type === 0 ? row.songId : row.songListId }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="up" label="点赞数" width="80" sortable>
        <template #default="{ row }">
          <el-tag :type="row.up > 100 ? 'success' : row.up > 50 ? 'warning' : ''">
            {{ row.up }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="日期" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="info" @click="showCommentDetail(row)">查看</el-button>
          <el-button type="warning" @click="editComment(row)">修改</el-button>
          <el-button type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination 
        background 
        layout="prev, pager, next, sizes, total" 
        :total="total"
        :page-size="queryParams.size"
        :current-page="queryParams.pageNo"
        :page-sizes="[5, 7, 10, 20]"
        @current-change="(page) => handlePaginationChange(page, queryParams.size)"
        @size-change="(size) => handlePaginationChange(1, size)"
      />
    </div>
  </el-card>

  <!-- 评论详情对话框 -->
  <el-dialog v-model="detailDialogVisible" title="评论详情" width="600px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="评论ID">{{ currentComment.id }}</el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ currentComment.userId }}</el-descriptions-item>
      <el-descriptions-item label="评论类型">
        {{ currentComment.type === 0 ? '歌曲评论' : '歌单评论' }}
      </el-descriptions-item>
      <el-descriptions-item label="歌曲ID" v-if="currentComment.type === 0">
        {{ currentComment.songId }}
      </el-descriptions-item>
      <el-descriptions-item label="歌单ID" v-if="currentComment.type === 1">
        {{ currentComment.songListId }}
      </el-descriptions-item>
      <el-descriptions-item label="评论内容">
        <div style="white-space: pre-wrap;">{{ currentComment.content }}</div>
      </el-descriptions-item>
      <el-descriptions-item label="点赞数">{{ currentComment.up }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(currentComment.createTime) }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <!-- 编辑评论对话框 -->
  <el-dialog v-model="editDialogVisible" title="修改评论" width="600px">
    <el-form :model="currentComment" label-width="100px">
      <el-form-item label="评论ID">
        <el-input v-model="currentComment.id" disabled />
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="currentComment.userId" disabled />
      </el-form-item>
      <el-form-item label="评论类型" prop="type">
        <el-radio-group v-model="currentComment.type" disabled>
          <el-radio :label="0">歌曲评论</el-radio>
          <el-radio :label="1">歌单评论</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <!-- 歌曲ID输入框 -->
      <el-form-item 
        v-if="currentComment.type === 0"
        label="歌曲ID" 
        prop="songId"
      >
        <el-input 
          v-model="currentComment.songId" 
          placeholder="请输入歌曲ID"
          disabled
        />
      </el-form-item>
      
      <!-- 歌单ID输入框 -->
      <el-form-item 
        v-if="currentComment.type === 1"
        label="歌单ID" 
        prop="songListId"
      >
        <el-input 
          v-model="currentComment.songListId" 
          placeholder="请输入歌单ID"
          disabled
        />
      </el-form-item>
      
      <el-form-item label="评论内容" prop="content" required>
        <el-input 
          v-model="currentComment.content" 
          type="textarea" 
          :rows="5" 
          placeholder="请输入评论内容"
          clearable
          show-word-limit
          maxlength="500"
        />
      </el-form-item>
      
      <el-form-item label="点赞数">
        <el-input-number v-model="currentComment.up" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">保存</el-button>
    </template>
  </el-dialog>

  <!-- 新增评论对话框 -->
  <el-dialog v-model="addDialogVisible" title="新增评论" width="600px">
    <el-form :model="newComment" label-width="100px">
      <el-form-item label="用户ID" prop="userId" required>
        <el-input 
          v-model.number="newComment.userId" 
          placeholder="请输入用户ID"
          type="number"
          min="1"
        />
      </el-form-item>
      
      <el-form-item label="评论类型" prop="type" required>
        <el-radio-group v-model="newComment.type">
          <el-radio :label="0">歌曲评论</el-radio>
          <el-radio :label="1">歌单评论</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <!-- 歌曲ID输入框 -->
      <el-form-item 
        v-if="newComment.type === 0"
        label="歌曲ID" 
        prop="songId"
        required
      >
        <el-input 
          v-model="newComment.songId" 
          placeholder="请输入歌曲ID"
        />
      </el-form-item>
      
      <!-- 歌单ID输入框 -->
      <el-form-item 
        v-if="newComment.type === 1"
        label="歌单ID" 
        prop="songListId"
        required
      >
        <el-input 
          v-model="newComment.songListId" 
          placeholder="请输入歌单ID"
        />
      </el-form-item>
      
      <el-form-item label="评论内容" prop="content" required>
        <el-input 
          v-model="newComment.content" 
          type="textarea" 
          :rows="5" 
          placeholder="请输入评论内容"
          clearable
          show-word-limit
          maxlength="500"
        />
      </el-form-item>
      
      <el-form-item label="点赞数">
        <el-input-number v-model="newComment.up" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="addDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAdd">添加</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.el-card {
  min-height: 100%;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.el-descriptions-item {
  padding: 10px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>