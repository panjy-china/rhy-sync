<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "@/api/user";
import { useUserStore } from "@/stores/user";
import {User,Lock} from '@element-plus/icons-vue'
const router=useRouter()

// 绑定form表单
const loginFormRef = ref(null);
// 绑定表单登录数据
const loginForm = ref({
  name: 'admin',
  password: '123'
})
// 校验规则
const rules = ref({
  name: [
    { required: true, message: '请输入登录名称', trigger: 'blur' },
    { min: 3, max: 5, message: '长度必须在 3 到 5', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 5, message: '密码必须在 3 到 15', trigger: 'blur' },
  ],
})
// 登录事件
const submitForm = async (formEl) => {
  if (!formEl) return
  // 表单校验
  await formEl.validate((valid, fields) => {
    if (valid) {
      // 调用登录接口
      login(loginForm.value).then(res => {
        if(res.code == 0){
          ElMessage.success(res.msg)
          const userStore = useUserStore()
          userStore.setToken(res.data)
          router.push('/')
        }else{
          ElMessage.error(res.msg)
        }
       
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
// 重置表单事件
const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}



</script>
<template>
  <div class="container">
    <div class="login">
      <h2>音乐管理后台</h2>
      <el-form ref="loginFormRef" style="max-width: 600px" :model="loginForm" :rules="rules" label-width="auto">
        <el-form-item label="" prop="name">
          <el-input :prefix-icon="User" v-model="loginForm.name" 
            placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="" prop="password">
          <el-input :prefix-icon="Lock" v-model="loginForm.password" type="password"
          placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm(loginFormRef)">
            登录
          </el-button>
          <el-button @click="resetForm(loginFormRef)">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<style scoped>
.container {
  height: 100vh;
  width: 100vw;
  background: url('@/assets/images/app-bg.jpg')   center/cover no-repeat fixed;
  padding-top: 120px;
}

.login h2 {
  margin-bottom: 20px;
}

.login {
  width: 400px;
  height: 260px;
  background: white;
  margin: 0 auto;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  padding: 20px;
}
</style>