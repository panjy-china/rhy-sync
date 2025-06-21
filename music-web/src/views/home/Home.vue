<script setup>
import { ref } from 'vue'
import {  RouterView } from 'vue-router'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 导入图标
import { Headset, VideoPlay, VideoCamera, ArrowDown } from '@element-plus/icons-vue'
const router = useRouter()

// 下拉菜单事件
const handleCommand = (command) => {
  const userStore = useUserStore()
  // 退出系统事件
  if (command === 'exit') {
    // 清除token
    userStore.setToken(null)
    // 跳转登录页面
    router.push('/login')
  }
}
</script>

<template>
  <el-container>
    <el-aside width="200px">
      <div class="logo"></div>
      <el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo"
        :default-active="$route.path" text-color="#fff" :router="true">
        <el-sub-menu index="">
          <template #title>
            <el-icon>
              <Headset />
            </el-icon>
            <span>音频视频</span>
          </template>
          <el-menu-item index="/song">
            <el-icon>
              <VideoPlay />
            </el-icon>
            <span>音频管理</span>
          </el-menu-item>
          <el-menu-item index="/vedio">
            <el-icon>
              <VideoCamera />
            </el-icon>
            <span>视频管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/singer">
          <el-icon>
            <VideoPlay />
          </el-icon>
          <span>歌手管理</span>
        </el-menu-item>
        <el-menu-item index="3">
          <el-icon>
            <VideoPlay />
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon>
            <VideoPlay />
          </el-icon>
          <span>评论管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <h3>
          音乐后台系统
        </h3>
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <img class="avatar" src="@/assets/images/default.png" alt="" srcset="">
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="changeAvatar">修改图像</el-dropdown-item>
              <el-dropdown-item divided command="exit">退出系统</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <RouterView></RouterView>
      </el-main>
      <el-footer>
        鲁东大学音乐软件管理系统@2025
      </el-footer>
    </el-container>
  </el-container>
</template>

<style scoped>
.el-container {
  height: 100vh;
}

.el-aside {
  background-color: #545c64;
  color: #333;
  text-align: center;
  line-height: 200px;
  height: 100%;
  border-right: 0;
}

.logo {
  width: 100%;
  height: 80px;
  background: url('@/assets/images/kgc-music.png') no-repeat center/70px 70px;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #333;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>