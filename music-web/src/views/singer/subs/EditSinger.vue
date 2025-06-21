<script setup>
import {ref} from 'vue'
import { addSinger } from '@/api/singer'

const drawer = ref(false)
const formLabelWidth = '80px'
// 表单数据
const form = ref({
  name: '',
  sex: "0",
  pic: '',
  birth: '',
  location: '',
  introduction: '',
})
// 定一个供父组件调用的打开抽屉的方法
const openDrawer = async () => {
  drawer.value = true
  // 异步请求

}
//  暴露方法
defineExpose({
  openDrawer
})
const upload = (url) => {  
  form.value.pic = url
}
const onSubmit =async () => {  
  const res = await addSinger(form.value)  
  console.log(res);  
  ElMessage.success(res.msg)
  drawer.value = false
}

</script>

<template>
  <el-drawer v-model="drawer" title="添加歌手" direction="rtl" size="40%">
    <div class="demo-drawer__content">
      <el-form :model="form">
        <el-form-item label="名称" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-radio-group v-model="form.sex">
            <el-radio value="0" size="large">男</el-radio>
            <el-radio value="1" size="large">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日" :label-width="formLabelWidth">
          <el-date-picker v-model="form.birth" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="国籍" :label-width="formLabelWidth">
          <el-input v-model="form.location" autocomplete="off" />
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth">
          <el-input v-model="form.introduction" autocomplete="off" />
        </el-form-item>
        <el-form-item label="图像" :label-width="formLabelWidth">
          <image-upload @upload-success="upload"></image-upload>
        </el-form-item>
        <el-form-item label="" :label-width="formLabelWidth">
          <el-button type="primary" @click="onSubmit">添加</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-drawer>
</template>

<style scoped>

</style>