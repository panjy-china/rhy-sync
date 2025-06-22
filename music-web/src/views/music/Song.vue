<script setup>
import { ref } from 'vue'
import { baseURL } from '@/api/request'
import { formatDate } from '@/utils/format'
import { getSongList, getSongDetail } from '@/api/song'

const songs = ref([])
const queryParams = ref({
  pageNo: 1,
  size: 7,
  search: ''
})
const total = ref(0)
const songList = async () => {
  const { data } = await getSongList(queryParams.value)
  songs.value = data.records
  total.value = data.total
}
songList()
// 分页
const change = (pageNo, size) => {
  console.log(pageNo, size);
  queryParams.value.pageNo = pageNo
  queryParams.value.size = size
  songList()
}
// 搜索
const submitForm = () => {
  songList()
}


// 歌曲列表
const songsList = ref([
]);

// 歌手映射表 {id: name}
const singersMap = ref({
  101: '歌手A',
  102: '歌手B'
});
const playingSong = async row => {
  const { data } = await getSongDetail(row.id)
  data.url = baseURL + '/r' + data.url
  data.pic = baseURL + '/r' + data.pic
  console.log(data);
  songsList.value.unshift(data)
  
}
// 边界
const editSong = async row => { 
}
// 删除
const deleteSong = async row => { 
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => { 
    // 调用删除接口
  })
}
</script>

<template>
  <el-card style="max-width: 100%">
    <template #header>
      <div class="card-header">
        <el-form :inline="true" :model="queryParams" label-width="auto" class="demo-form-inline">
          <el-form-item label="歌名">
            <el-input placeholder="请输入歌曲信息" clearable v-model="queryParams.search" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm">搜</el-button>
          </el-form-item>
        </el-form>
      </div>
    </template>
    <el-table :data="songs" style="width: 100%">
      <el-table-column prop="id" label="编号" />
      <el-table-column label="歌名">
        <template #default="{ row }">
          <el-link type="primary" @click="playingSong(row)">{{ row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="日期" :formatter="formatDate" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" @click="editSong(row)">编辑</el-button>
          <el-button type="danger" @click="deleteSong(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="example-pagination-block">
      <el-pagination background layout="prev, pager, next" @change="change" :total="total"
        :page-size="queryParams.size" />
    </div>
  </el-card>
  <!-- 音乐播放器组件 -->
  <MusicPlayer :songs="songsList" :singers="singersMap" :autoPlay="true" />
</template>

<style scoped>
.el-card {
  min-height: 100%;
  width: 100vw;
}
</style>