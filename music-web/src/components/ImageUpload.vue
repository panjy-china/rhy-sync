<script setup>
import { ref } from 'vue'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { reqeust } from '@/api/request'

const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const disabled = ref(false)

const handleChange = (file, files) => {
  fileList.value = files
}

const handleRemove = (file) => {
  fileList.value = fileList.value.filter((item) => item.uid !== file.uid)
}

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传一张图片')
}

const submitUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择图片后再上传')
    return
  }

  const formData = new FormData()
  formData.append('file', fileList.value[0].raw)


  const response = await reqeust({
    url:'/singer/upload', 
    method: 'POST',
    data:formData, 
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

  ElMessage.success('上传成功')
  console.log('上传结果:', response.data)
  // 这里可以触发父组件的事件或处理上传成功后的逻辑
  emit('upload-success', response.data)

}

// 定义emit
const emit = defineEmits(['upload-success'])
</script>
<template>
  <div class="image-upload-container">
    <el-upload class="upload-demo" action="" :auto-upload="false" :on-change="handleChange" :file-list="fileList"
      list-type="picture-card" :limit="1" :on-exceed="handleExceed" accept="image/*">
      <el-icon>
        <Plus />
      </el-icon>
      <template #file="{ file }">
        <div>
          <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
          <span class="el-upload-list__item-actions">
            <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
              <el-icon><zoom-in /></el-icon>
            </span>
            <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
              <el-icon>
                <Delete />
              </el-icon>
            </span>
          </span>
        </div>
      </template>
    </el-upload>

    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" />
    </el-dialog>

    <el-button type="primary" @click="submitUpload">上传图片</el-button>
  </div>
</template>

<style scoped>
.image-upload-container {
  margin: 20px;
}
</style>