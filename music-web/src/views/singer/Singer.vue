<script setup>
import { ref } from 'vue'
import { formatDate } from '@/utils/format'
import { getSingerList, getSingerDetail } from '@/api/singer'
import SingerDetail from './subs/SingerDetail.vue'
import EditSinger from './subs/EditSinger.vue'
const singers = ref([])
const queryParams = ref({
  pageNo: 1,
  size: 6,
  search: ''
})
const total = ref(0)
const singersList = async () => {
  const { data } = await getSingerList(queryParams.value)
  singers.value = data.records
  total.value = data.total
}
singersList()
// 分页
const change = (pageNo, size) => {
  console.log(pageNo, size);
  queryParams.value.pageNo = pageNo
  queryParams.value.size = size
  singersList()
}
// 搜索
const submitForm = () => {
  singersList()
}
const singerDetail = ref(null)
// 显示抽屉
const showDetail = (row) => {
  // 执行子组件方法
  singerDetail.value.openDrawer(row)
}
const editSinger = ref(null)
const addSinger = () => {
  editSinger.value.openDrawer()
}
</script>

<template>
  <el-card style="max-width: 100%">
    <template #header>
      <div class="card-header">
        <el-form :inline="true" :model="queryParams" label-width="auto" class="demo-form-inline">
          <el-form-item label="歌手">
            <el-input placeholder="请输入歌手信息" clearable v-model="queryParams.search" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm">搜</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="addSinger">添加</el-button>
      </div>
    </template>
    <el-table :data="singers" style="width: 100%">
      <el-table-column label="编号">
        <template #default="{ row }">
          <el-link type="primary" @click="showDetail(row)">{{ row.id }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="歌名" />
      <el-table-column prop="birth" label="生日" :formatter="formatDate" />
    </el-table>
    <div class="example-pagination-block">
      <el-pagination background layout="prev, pager, next" @change="change" :total="total"
        :page-size="queryParams.size" />
    </div>
  </el-card>
<!-- 抽屉详情 -->
 <singer-detail ref="singerDetail" />
 <!-- 添加歌手 -->
 <edit-singer ref="editSinger" />
</template>

<style scoped>
.el-card {
  min-height: 100%;
  width: 100vw;
}
.card-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>