<script setup>
import { ref } from 'vue'
import { getSingerDetail } from '@/api/singer'
import { baseURL } from '@/api/request'

// 是否显示抽屉
const drawer = ref(false)
const singer = ref({})
// 定一个供父组件调用的打开抽屉的方法
const openDrawer = async (row) => {
  drawer.value = true
  // 异步请求
  const { data } = await getSingerDetail(row.id)
  singer.value = data

}
//  暴露方法
defineExpose({
  openDrawer
})
</script>

<template>
  <el-drawer v-model="drawer" direction="rtl" size="50%">
    <template #header>
      <h4>歌手详情信息</h4>
    </template>
    <template #default>
      <div>
        <div>歌手名称：{{ singer.name }}</div>
        <div>国家：{{ singer.location }}</div>
        <div class="pic">         
          <img :src="baseURL+'/r' +singer.pic" alt=""></img>
        </div>
      </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="drawer = false">关闭</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style scoped>
.pic img{
  width: 120pz;
  height: 120px;  

}
</style>