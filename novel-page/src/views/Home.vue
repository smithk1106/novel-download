<template>
  <div class="min-h-screen bg-gradient-to-b from-white/80 to-blue-100/60 dark:from-black/60 dark:to-gray-900/80 ">
    <div class="container mx-auto px-4 py-16">
      <!-- 标���区域 - 苹果风格大标题 -->
      <div class="text-center mb-16">
        <h1 class="text-5xl md:text-6xl  text-gray-900 dark:text-white mb-6 tracking-tight drop-shadow-[0_2px_8px_rgba(0,0,0,0.04)]">小说下载管理</h1>
        <p class="text-xl md:text-2xl text-gray-500 dark:text-gray-300 max-w-2xl mx-auto ">优雅而简单的小说管理工具，让阅读体验更加轻松自在</p>
      </div>

      <!-- 功能特点区域 - 玻璃拟态卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
        <!-- 功能卡片 1 -->
        <div class="glass-card">
          <div class="text-blue-500 mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
            </svg>
          </div>
          <h3 class="text-xl  text-gray-900 dark:text-white mb-2">智能下载</h3>
          <p class="text-gray-500 dark:text-gray-300">支持多个小说源，自动识别章节，智能分析内容</p>
        </div>
        <!-- 功能卡片 2 -->
        <div class="glass-card">
          <div class="text-purple-500 mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <h3 class="text-xl  text-gray-900 dark:text-white mb-2">本地管理</h3>
          <p class="text-gray-500 dark:text-gray-300">完善的本地书库管理，支持分类、标签、收藏</p>
        </div>
        <!-- 功能卡片 3 -->
        <div class="glass-card">
          <div class="text-green-500 mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </div>
          <h3 class="text-xl  text-gray-900 dark:text-white mb-2">自动更新</h3>
          <p class="text-gray-500 dark:text-gray-300">自动检查小说更新，及时推送最新章节提醒</p>
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="text-center space-y-4">
        <button 
          @click="handleNavigate"
          class="glass-btn px-8 py-4 text-lg "
        >
          开始使用
        </button>
        <p class="text-sm text-gray-500 dark:text-gray-400">简单几步，开启您的阅读之旅</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleNavigate = () => {
  router.push('/pages/crawler')
}
</script>

<style scoped>


.glass-card {
  @apply rounded-2xl p-8 shadow-lg transition-shadow duration-300 relative overflow-hidden;
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(18px) saturate(160%);
  -webkit-backdrop-filter: blur(18px) saturate(160%);
  border: 1.5px solid rgba(255,255,255,0.35);
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.08), 0 1.5px 0 0 rgba(255,255,255,0.18) inset;
}
.dark .glass-card {
  background: rgba(30,32,38,0.45);
  border: 1.5px solid rgba(255,255,255,0.12);
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.18), 0 1.5px 0 0 rgba(255,255,255,0.08) inset;
}

.glass-btn {
  @apply bg-white/60 dark:bg-black/40 text-gray-900 dark:text-white rounded-full shadow-lg hover:bg-white/80 dark:hover:bg-black/60 transition-all duration-300 border border-white/40 dark:border-black/40;
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
}

@media (max-width: 768px) {
  .glass-card {
    padding: 1.5rem;
  }
  h1 {
    font-size: 2.25rem;
  }
}
</style>

