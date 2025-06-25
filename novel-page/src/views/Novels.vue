<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">
    <div class="container mx-auto px-4 py-16">
      <!-- 页面标题区域 -->
      <div class="mb-16 text-center">
        <h1 class="text-5xl font-bold bg-gradient-to-r from-slate-800 to-slate-600 bg-clip-text text-transparent mb-4">
          我的书库
        </h1>
        <p class="text-lg text-slate-600 font-light">探索您收藏的小说世界</p>
      </div>

      <!-- 搜索框 - 液态玻璃风格 -->
      <div class="mb-12">
        <div class="max-w-lg mx-auto">
          <div class="relative group">
            <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-2xl border border-white/30 shadow-2xl"></div>
            <div class="relative flex items-center">
              <input 
                v-model="searchQuery"
                type="text"
                placeholder="搜索小说名称..."
                class="w-full pl-6 pr-14 py-4 bg-white/10 backdrop-blur-sm border-0 rounded-2xl text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/20 transition-all duration-300"
                @keyup.enter="handleSearch"
              />
              <button 
                @click="handleSearch"
                class="absolute right-3 p-2 text-slate-400 hover:text-blue-500 transition-all duration-300 rounded-xl hover:bg-white/20"
                :class="{ 'animate-spin': isSearching }"
              >
                <svg 
                  v-if="!isSearching"
                  class="w-6 h-6" 
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                </svg>
                <svg 
                  v-else 
                  class="w-6 h-6" 
                  fill="none" 
                  viewBox="0 0 24 24"
                >
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 书籍网格 - 液态玻璃风格 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-8">
        <div v-for="novel in novels" :key="novel.id" 
          class="group relative"
        >
          <!-- 液态玻璃背景 -->
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl group-hover:shadow-3xl transition-all duration-500"></div>
          
          <div class="relative p-6">
            <!-- 封面图片 -->
            <div class="relative mb-6 overflow-hidden rounded-2xl">
              <img 
                :src="getCoverUrl(novel.cover)"
                :alt="novel.name"
                class="w-full aspect-[3/4] object-cover transition-transform duration-500 group-hover:scale-105"
                @error="handleImageError"
              />
              <!-- 悬浮操作按钮 -->
              <div class="absolute inset-0 bg-black/40 backdrop-blur-sm opacity-0 group-hover:opacity-100 transition-all duration-500 flex flex-col items-center justify-center space-y-3">
                <a 
                  :href="getFullUrl(API_URLS.NOVEL.DOWNLOAD(novel.id))"
                  class="bg-white/90 backdrop-blur-sm text-slate-700 px-6 py-3 rounded-full text-sm font-medium hover:bg-white transition-all duration-300 flex items-center space-x-2 shadow-lg"
                  download
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/>
                  </svg>
                  <span>下载</span>
                </a>
                <button 
                  @click="confirmDelete(novel)"
                  class="bg-red-500/90 backdrop-blur-sm text-white px-6 py-3 rounded-full text-sm font-medium hover:bg-red-600 transition-all duration-300 flex items-center space-x-2 shadow-lg"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                  <span>删除</span>
                </button>
              </div>
            </div>
            
            <!-- 书籍信息 -->
            <div class="text-center">
              <h3 class="font-semibold text-slate-800 text-lg mb-2 line-clamp-2">{{ novel.name }}</h3>
              <p class="text-slate-600 text-sm font-light">{{ novel.author }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页控件 - 液态玻璃风格 -->
      <div v-if="total > pageSize" class="mt-16 flex justify-center">
        <div class="relative">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-2xl border border-white/30 shadow-xl"></div>
          <div class="relative flex items-center space-x-4 p-2">
            <button 
              @click="handlePageChange(current - 1)"
              :disabled="current === 1"
              class="px-6 py-3 bg-white/20 backdrop-blur-sm text-slate-700 rounded-xl hover:bg-white/30 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 font-medium"
            >
              上一页
            </button>
            <span class="px-4 py-3 bg-white/30 backdrop-blur-sm text-slate-700 rounded-xl font-medium">
              {{ current }} / {{ totalPages }}
            </span>
            <button 
              @click="handlePageChange(current + 1)"
              :disabled="current === totalPages"
              class="px-6 py-3 bg-white/20 backdrop-blur-sm text-slate-700 rounded-xl hover:bg-white/30 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 font-medium"
            >
              下一页
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 - 液态玻璃风格 -->
      <div v-if="novels.length === 0" class="text-center py-20">
        <div class="relative max-w-md mx-auto">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl"></div>
          <div class="relative p-12">
            <div class="text-slate-400 mb-6">
              <svg class="w-20 h-20 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <h3 class="text-2xl font-semibold text-slate-800 mb-3">暂无小说</h3>
            <p class="text-slate-600 mb-8 font-light">去搜索下载一些小说吧</p>
            <router-link 
              to="/pages/crawler"
              class="inline-block px-8 py-4 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-2xl hover:from-blue-600 hover:to-indigo-700 transition-all duration-300 font-medium shadow-lg hover:shadow-xl"
            >
              去下载
            </router-link>
          </div>
        </div>
      </div>

      <!-- 删除确认对话框 - 液态玻璃风格 -->
      <div v-if="deleteDialog.show" class="fixed inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center z-50">
        <div class="relative max-w-md w-full mx-4">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl"></div>
          <div class="relative p-8">
            <h3 class="text-2xl font-semibold text-slate-800 mb-4">确认删除</h3>
            <p class="text-slate-800 mb-8 font-light">确定要删除《{{ deleteDialog.novel?.name }}》吗？此操作不可恢复。</p>
            <div class="flex justify-end space-x-4">
              <button 
                @click="deleteDialog.show = false"
                class="px-6 py-3 bg-white/20 backdrop-blur-sm text-slate-700 rounded-xl hover:bg-white/30 transition-all duration-300 font-medium"
              >
                取消
              </button>
              <button 
                @click="handleDelete"
                class="px-6 py-3 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl hover:from-red-600 hover:to-red-700 transition-all duration-300 font-medium shadow-lg"
                :disabled="isDeleting"
              >
                {{ isDeleting ? '删除中...' : '确认删除' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Toast 提示 - 液态玻璃风格 -->
      <div 
        v-if="toast.show"
        class="fixed bottom-6 right-6 flex items-center p-4 space-x-3 text-sm rounded-2xl shadow-2xl transition-all duration-300 backdrop-blur-xl"
        :class="{
          'bg-green-500/20 border border-green-300/30 text-green-800': toast.type === 'success',
          'bg-red-500/20 border border-red-300/30 text-red-800': toast.type === 'error'
        }"
      >
        <svg 
          v-if="toast.type === 'success'"
          class="w-5 h-5 text-green-600" 
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
        </svg>
        <svg 
          v-else
          class="w-5 h-5 text-red-600" 
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
        </svg>
        <p class="font-medium">{{ toast.message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { debounce } from 'lodash'
import defaultCover from '@/assets/defaultCover.webp'
import { API_BASE_URL, API_URLS, getFullUrl } from '@/config/api'

const novels = ref([])
const searchQuery = ref('')
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const isSearching = ref(false)
const isDeleting = ref(false)
const deleteDialog = ref({
  show: false,
  novel: null
})
const toast = ref({
  show: false,
  type: 'success',
  message: ''
})

// 获取封面图片
const getCoverUrl = (cover) => {
  return cover || defaultCover
}

// 获取小说列表
const fetchNovels = async () => {
  try {
    isSearching.value = true
    const response = await fetch(getFullUrl(API_URLS.NOVEL.LIST), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: searchQuery.value,
        pageNo: current.value,
        pageSize: pageSize.value
      })
    })
    const result = await response.json()
    if (result.code === 200) {
      novels.value = result.data.records
      total.value = result.data.total
      totalPages.value = result.data.pages
      current.value = result.data.current
    }
  } catch (error) {
    console.error('获取小说列表失败:', error)
  } finally {
    isSearching.value = false
  }
}

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.src = defaultCover
}

// 处理搜索
const handleSearch = debounce(async () => {
  isSearching.value = true
  current.value = 1 // 重置页码
  try {
    await fetchNovels()
  } finally {
    isSearching.value = false
  }
}, 300)

// 处理页码变化
const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  current.value = page
  fetchNovels()
}

// 显示提示
const showToast = (message, type = 'success') => {
  toast.value = {
    show: true,
    type,
    message
  }
  setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

// 显示删除确认框
const confirmDelete = (novel) => {
  deleteDialog.value = {
    show: true,
    novel
  }
}

// 处理删除
const handleDelete = async () => {
  if (!deleteDialog.value.novel) return
  
  try {
    isDeleting.value = true
    const response = await fetch(getFullUrl(API_URLS.NOVEL.DELETE(deleteDialog.value.novel.id)), {
      method: 'POST'
    })
    const result = await response.json()
    
    if (result.code === 200) {
      showToast('删除成功')
      // 重新加载列表
      fetchNovels()
    } else {
      showToast(result.msg || '删除失败', 'error')
    }
  } catch (error) {
    console.error('删除失败:', error)
    showToast('删除失败，请重试', 'error')
  } finally {
    isDeleting.value = false
    deleteDialog.value.show = false
  }
}

onMounted(() => {
  fetchNovels()
})
</script>

<style scoped>
/* 液态玻璃效果增强 */
@supports (backdrop-filter: blur(20px)) {
  .backdrop-blur-xl {
    backdrop-filter: blur(20px);
  }
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.3);
  border-radius: 4px;
  backdrop-filter: blur(10px);
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.5);
}

/* 动画效果 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式字体 */
@media (max-width: 640px) {
  .text-5xl {
    font-size: 2.5rem;
  }
}

@media (max-width: 480px) {
  .text-5xl {
    font-size: 2rem;
  }
}
</style> 