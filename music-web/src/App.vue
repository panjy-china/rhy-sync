<script setup>
import { RouterLink, RouterView } from 'vue-router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <!-- 路由出口 -->
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>

</template>

<style scoped>
</style>
