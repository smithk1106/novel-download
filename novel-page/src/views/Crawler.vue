<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { WS_URL } from '@/config/api'

const searchQuery = ref('')
const searchType = ref('fuzzy') // 新增搜索类型，默认为模糊搜索
const isLoading = ref(false)
const messages = ref([])
const novelCount = ref(0)
const connectionStatus = ref('未连接')
const messageContainer = ref(null)
const downloadContainer = ref(null) // 添加下载消息容器引用
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5
const baseReconnectDelay = 1000 // 初始重连延迟(毫秒)
let ws = null
let reconnectTimeout = null

// 解析小说信息
const parseNovelInfo = (content) => {
  try {
    const data = JSON.parse(content)
    const novelInfo = JSON.parse(data.content)
    return {
      id: data.id,
      sourceId: data.sourceId,
      isStop: data.isStop,
      title: novelInfo.bookName,
      author: novelInfo.author.replace('作者：', ''),
      latestChapter: novelInfo.latestChapter,
      url: novelInfo.url,

    }
  } catch (error) {
    console.error('解析小说信息失败:', error)
    return null
  }
}

// 初始化WebSocket连接
const initWebSocket = () => {
  if (ws?.readyState === WebSocket.CONNECTING) return

  ws = new WebSocket(WS_URL)
  
  ws.onopen = () => {
    connectionStatus.value = '已连接'
    reconnectAttempts.value = 0 // 重置重连次数
    if (reconnectTimeout) {
      clearTimeout(reconnectTimeout)
      reconnectTimeout = null
    }
    addMessage({ 
      type: 'console', 
      content: '连接成功，请输入小说名称开始搜索' 
    })
  }
  
  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    if (data.type === 'NovelNameSearchResultMessageListener') {
      const novelInfo = parseNovelInfo(data.content)
      if (novelInfo) {
        novelCount.value++
        addMessage({ 
          type: 'novel',
          content: novelInfo,
          isNew: true 
        })
        if (novelInfo.isStop) {
          isLoading.value = false
          addMessage({ 
            type: 'console',
            content: '搜索完成',
            isNew: true 
          })
        }
      }
    } else if (data.type === 'NovelNameSearchConsoleMessageListener') {
      // const consoleResult = JSON.parse(data.content)
      const consoleMessage = data.content
      addMessage({ 
        type: 'console',
        content: consoleMessage,
        isNew: true 
      })
      
      if (consoleMessage.includes('错误') || 
          consoleMessage.includes('搜索完成')) {
        isLoading.value = false
      }
    } else if (data.type === 'NovelDownloadConsoleMessageListener') {
      addMessage({ 
        type: 'download',
        content: data.content,
        isNew: true 
      })
      // 下载消息自动滚动到底部
      scrollToBottomDownload()
    }
    scrollToBottom()
  }
  
  ws.onclose = () => {
    connectionStatus.value = '连接已断开'
    isLoading.value = false

    if (reconnectAttempts.value < maxReconnectAttempts) {
      const delay = Math.min(baseReconnectDelay * Math.pow(2, reconnectAttempts.value), 30000) // 最大延迟30秒
      reconnectAttempts.value++
      
      addMessage({ 
        type: 'console', 
        content: `连接已断开，${reconnectAttempts.value}秒后尝试第${reconnectAttempts.value}次重连...` 
      })

      reconnectTimeout = setTimeout(() => {
        initWebSocket()
      }, delay)
    } else {
      addMessage({ 
        type: 'console', 
        content: '重连次数已达上限，请刷新页面重试' 
      })
    }
  }
  
  ws.onerror = () => {
    isLoading.value = false
  }
}

// 添加消息
const addMessage = (message) => {
  messages.value.push(message)
  // console.log('新消息:', message)
  if (message.isNew) {
    setTimeout(() => {
      message.isNew = false
    }, 5000)
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// 下载区域滚动到底部
const scrollToBottomDownload = () => {
  if (downloadContainer.value) {
    downloadContainer.value.scrollTop = downloadContainer.value.scrollHeight
  }
}

// 开始爬取
const startCrawling = () => {
  if (!searchQuery.value.trim()) return
  
  isLoading.value = true
  messages.value = []
  novelCount.value = 0

  ws.send(JSON.stringify({
    type: 'NovelNameSearchMessageListener',
    content: {
      bookName: searchQuery.value,
      searchType: searchType.value // 添加搜索类型参数
    }
  }))
}

// 下载小说
const downloadNovel = (novel) => {
  ws.send(JSON.stringify({
    type: 'NovelDownloadMessageListener',
    content: {"searchResultId": novel.id},
  }))
}

onMounted(() => {
  initWebSocket()
})

onUnmounted(() => {
  if (reconnectTimeout) {
    clearTimeout(reconnectTimeout)
  }
  if (ws) {
    ws.close()
  }
})




// 下载配置相关
const showDownloadModal = ref(false)
const selectedNovel = ref({})
const downloadActiveTab = ref(0)
const downloadTabs = [
  { label: '全本下载', type: 0 },
  { label: '指定章节', type: 1 },
  { label: '最新章节', type: 2 }
]
const downloadConfig = ref({
  startChapter: 1,
  endChapter: 100,
  latestChapterCount: 10
})

// 打开下载配置弹窗
const openDownloadModal = (novel) => {
  selectedNovel.value = novel
  showDownloadModal.value = true
  // 重置配置
  downloadActiveTab.value = 0
  downloadConfig.value = {
    startChapter: 1,
    endChapter: 100,
    latestChapterCount: 10
  }
}

// 关闭下载配置弹窗
const closeDownloadModal = () => {
  showDownloadModal.value = false
}

// 选择下载类型标签
const selectDownloadTab = (index) => {
  downloadActiveTab.value = index
}

// 确认下载
const confirmDownload = () => {
  const downloadType = downloadTabs[downloadActiveTab.value].type

  const downloadParams = {
    searchResultId: selectedNovel.value.id,
    downloadType: downloadType
  }

  // 根据选择的类型添加额外参数
  if (downloadType === 1) { // 指定章节
    downloadParams.startChapter = parseInt(downloadConfig.value.startChapter) || 1
    downloadParams.endChapter = parseInt(downloadConfig.value.endChapter) || 100
  } else if (downloadType === 2) { // 最新章节
    downloadParams.latestChapterCount = parseInt(downloadConfig.value.latestChapterCount) || 10
  }

  // 发送下载请求
  ws.send(JSON.stringify({
    type: 'NovelDownloadMessageListener',
    content: downloadParams
  }))

  // 关闭弹窗
  closeDownloadModal()
}

</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">
    <div class="container mx-auto px-4 py-16">
      <!-- 搜索区域 - 液态玻璃风格 -->
      <div class="max-w-3xl mx-auto mb-12">
        <div class="relative group">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl"></div>
          <div class="relative p-6 sm:p-8">
            <div class="flex flex-col sm:flex-row gap-4">
              <div class="flex-1 flex flex-col sm:flex-row gap-4">
                <div class="relative flex-1">
                  <input
                    v-model="searchQuery"
                    type="text"
                    placeholder="输入小说名称或作者"
                    @keyup.enter="startCrawling"
                    class="w-full pl-6 pr-14 py-4 bg-white/10 backdrop-blur-sm border-0 rounded-2xl text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/20 transition-all duration-300"
                  />
                  <button
                    @click="startCrawling"
                    class="absolute right-3 top-1/2 -translate-y-1/2 p-2 text-slate-400 hover:text-blue-500 transition-all duration-300 rounded-xl hover:bg-white/20"
                    :disabled="isLoading"
                  >
                    <svg v-if="!isLoading" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                    <svg v-else class="animate-spin h-6 w-6" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                  </button>
                </div>
                <select
                  v-model="searchType"
                  class="w-full sm:w-auto px-6 py-4 bg-white/10 backdrop-blur-sm border-0 rounded-2xl text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/20 transition-all duration-300 appearance-none cursor-pointer pr-12"
                  style="background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%2224%22%20height%3D%2224%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22%23666%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%3E%3Cpolyline%20points%3D%226%209%2012%2015%2018%209%22%3E%3C%2Fpolyline%3E%3C%2Fsvg%3E'); background-repeat: no-repeat; background-position: right 12px center; background-size: 16px;"
                >
                  <option value="fuzzy">模糊搜索</option>
                  <option value="exact">精确搜索</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>


      <!-- 主体内容区域：搜索结果和下载进度 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- 搜索结果展示区域 - 液态玻璃风格 -->
        <div class="relative">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl"></div>
          <div class="relative p-8">
            <h2 class="text-2xl font-semibold text-slate-800 mb-6">搜索结果</h2>
            <div
              ref="messageContainer"
              class="bg-white/30 backdrop-blur-lg rounded-xl p-4 h-96 overflow-y-auto space-y-4 mb-6"
            >
              <div
                v-for="(message, index) in messages"
                :key="index"
                class="message-item"
                :class="{'fade-in': message.isNew}"
              >
                <!-- 小说信息卡片 -->
<!--                <div v-if="message.type === 'novel'"-->
<!--                  class="bg-white/80 backdrop-blur-md rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow duration-300"-->
<!--                >-->
<!--                  <div class="flex items-start gap-4">-->
<!--                    <div class="flex-1">-->
<!--                      <h3 class="text-lg font-semibold text-slate-800">{{ message.content.title }}</h3>-->
<!--                      <p class="text-sm text-slate-500 mb-2">{{ message.content.author }}</p>-->
<!--                      <div class="mt-2 flex items-center gap-4">-->
<!--                        <span class="text-sm text-slate-500">最新章节：{{ message.content.latestChapter }}</span>-->
<!--                        <a-->
<!--                          :href="message.content.url"-->
<!--                          target="_blank"-->
<!--                          class="text-sm text-blue-500 hover:text-blue-600"-->
<!--                        >-->
<!--                          查看源网站-->
<!--                        </a>-->
<!--                      </div>-->
<!--                    </div>-->
<!--                    <button-->
<!--                        @click="openDownloadModal(message.content)"-->
<!--                        class="flex-shrink-0 bg-gradient-to-r from-purple-400 to-indigo-500 text-white px-4 py-2 rounded-lg font-medium hover:from-purple-500 hover:to-indigo-600 transition-all duration-300"-->
<!--                    >-->
<!--                      抓取-->
<!--                    </button>-->
<!--                  </div>-->
<!--                </div>-->
                <div v-if="message.type === 'novel'"
                     class="bg-white/80 backdrop-blur-md rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow duration-300 border border-white/50"
                >
                  <div class="flex items-start gap-4">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                        </svg>
                        <h3 class="text-lg font-semibold text-slate-800">{{ message.content.title }}</h3>
                      </div>
                      <div class="flex items-center text-sm text-slate-500 mb-2">
                        <svg class="w-4 h-4 mr-1 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <span>{{ message.content.author }}</span>
                      </div>
                      <div class="mt-2 flex items-center gap-4 text-sm">
                        <div class="flex items-center text-slate-500">
                          <svg class="w-4 h-4 mr-1 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                          </svg>
                          <span>最新章节：{{ message.content.latestChapter }}</span>
                        </div>
                        <a
                            :href="message.content.url"
                            target="_blank"
                            class="flex items-center text-blue-400 hover:text-blue-500 transition-colors"
                        >
                          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                          </svg>
                          查看源网站
                        </a>
                      </div>
                    </div>
<!--                    px-4 py-2 rounded-lg bg-gradient-to-r from-blue-400 to-cyan-500 text-white hover:from-blue-500 hover:to-cyan-600 transition-all duration-300 shadow-lg shadow-blue-500/20-->
                    <button
                        @click="openDownloadModal(message.content)"
                        class=" bg-gradient-to-r from-blue-300 to-cyan-400 text-white px-4 py-2 rounded-lg font-medium hover:from-blue-400 hover:to-cyan-500 transition-all duration-300 flex items-center gap-1 shadow-sm shadow-blue-500/20"
                    >

                      抓取
                    </button>
                  </div>
                </div>
                <!-- 控制台消息 -->
                <div v-else-if="message.type === 'console'"
                  class="text-sm py-2 px-3 rounded-lg font-medium"
                  :class="{
                    'bg-blue-100/80 text-blue-700': message.content.includes('成功'),
                    'bg-yellow-100/80 text-yellow-700': message.content.includes('搜索') || message.content.includes('数据源'),
                    'bg-red-100/80 text-red-700': message.content.includes('错误') || message.content.includes('失败'),
                    'bg-slate-100/80 text-slate-700': !message.content.includes('成功') && !message.content.includes('搜索') && !message.content.includes('错误') && !message.content.includes('失败')
                  }"
                >
                  {{ message.content }}
                </div>
              </div>
            </div>
            <div class="flex justify-between items-center text-sm text-slate-500">
              <span>已找到 {{ novelCount }} 本小说</span>
              <span>{{ connectionStatus }}</span>
            </div>
          </div>
        </div>

        <!-- 下载进度展示区域 - 液态玻璃风格 -->
        <div class="relative">
          <div class="absolute inset-0 bg-white/20 backdrop-blur-xl rounded-3xl border border-white/30 shadow-2xl"></div>
          <div class="relative p-8">
            <h2 class="text-2xl font-semibold text-slate-800 mb-6">下载进度</h2>
            <div ref="downloadContainer" class="bg-white/30 backdrop-blur-lg rounded-xl p-4 h-96 overflow-y-auto">
              <div class="space-y-3">
                <div
                  v-for="(message, index) in messages.filter(m => m.type === 'download')"
                  :key="index"
                  class="download-message"
                  :class="{'fade-in': message.isNew}"
                >
                  <div class="flex items-start gap-3 p-3 bg-white/80 backdrop-blur-md rounded-lg shadow-sm">
                    <div class="text-blue-500 flex-shrink-0 mt-1">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4 4m0 0l-4-4m4 4V4" />
                      </svg>
                    </div>
                    <div class="flex-1">
                      <p class="text-sm text-slate-700">{{ message.content }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="mt-4 p-4 bg-white/30 backdrop-blur-lg rounded-xl">
              <div class="flex justify-between items-center text-sm text-slate-500">
                <span>下载消息数: {{ messages.filter(m => m.type === 'download').length }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

<!--  &lt;!&ndash; 下载配置弹窗 &ndash;&gt;-->
<!--  <div v-if="showDownloadModal" class="fixed inset-0 z-50 flex items-center justify-center">-->
<!--    &lt;!&ndash; 背景遮罩 &ndash;&gt;-->
<!--    <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="closeDownloadModal"></div>-->

<!--    &lt;!&ndash; 弹窗内容 - 液态玻璃风格 &ndash;&gt;-->
<!--    <div class="relative w-full max-w-md mx-4 bg-white/20 backdrop-blur-xl border border-white/30 rounded-2xl shadow-2xl overflow-hidden">-->
<!--      <div class="p-6">-->
<!--        <h3 class="text-xl font-semibold text-slate-800 mb-4">下载设置</h3>-->

<!--        &lt;!&ndash; 标签页切换 &ndash;&gt;-->
<!--        <div class="flex border-b border-white/30 mb-6">-->
<!--          <button-->
<!--              v-for="(tab, index) in downloadTabs"-->
<!--              :key="index"-->
<!--              @click="selectDownloadTab(index)"-->
<!--              class="px-4 py-2 font-medium text-sm transition-all duration-300"-->
<!--              :class="downloadActiveTab === index ?-->
<!--            'border-b-2 border-purple-500 text-purple-700' :-->
<!--            'text-slate-600 hover:text-purple-500'"-->
<!--          >-->
<!--            {{ tab.label }}-->
<!--          </button>-->
<!--        </div>-->

<!--        &lt;!&ndash; 全本下载 &ndash;&gt;-->
<!--        <div v-if="downloadActiveTab === 0" class="space-y-4">-->
<!--          <p class="text-slate-700">将下载《{{ selectedNovel.title }}》的全部章节</p>-->
<!--        </div>-->

<!--        &lt;!&ndash; 指定章节下载 &ndash;&gt;-->
<!--        <div v-else-if="downloadActiveTab === 1" class="space-y-4">-->
<!--          <div class="flex space-x-4">-->
<!--            <div class="w-1/2">-->
<!--              <label class="block text-sm text-slate-600 mb-1">开始章节</label>-->
<!--              <input-->
<!--                  v-model="downloadConfig.startChapter"-->
<!--                  type="number"-->
<!--                  min="1"-->
<!--                  class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-purple-400/50"-->
<!--                  placeholder="开始章节"-->
<!--              />-->
<!--            </div>-->
<!--            <div class="w-1/2">-->
<!--              <label class="block text-sm text-slate-600 mb-1">结束章节</label>-->
<!--              <input-->
<!--                  v-model="downloadConfig.endChapter"-->
<!--                  type="number"-->
<!--                  min="1"-->
<!--                  class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-purple-400/50"-->
<!--                  placeholder="结束章节"-->
<!--              />-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->

<!--        &lt;!&ndash; 最新章节下载 &ndash;&gt;-->
<!--        <div v-else-if="downloadActiveTab === 2" class="space-y-4">-->
<!--          <div>-->
<!--            <label class="block text-sm text-slate-600 mb-1">下载最新章节数量</label>-->
<!--            <input-->
<!--                v-model="downloadConfig.latestChapterCount"-->
<!--                type="number"-->
<!--                min="1"-->
<!--                class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-purple-400/50"-->
<!--                placeholder="例如: 10"-->
<!--            />-->
<!--          </div>-->
<!--        </div>-->

<!--        &lt;!&ndash; 按钮区域 &ndash;&gt;-->
<!--        <div class="flex justify-end space-x-3 mt-6">-->
<!--          <button-->
<!--              @click="closeDownloadModal"-->
<!--              class="px-4 py-2 rounded-lg bg-white/30 backdrop-blur-sm text-slate-700 hover:bg-white/40 transition-all duration-300"-->
<!--          >-->
<!--            取消-->
<!--          </button>-->
<!--          <button-->
<!--              @click="confirmDownload"-->
<!--              class="px-4 py-2 rounded-lg bg-gradient-to-r from-purple-500 to-indigo-600 text-white hover:from-purple-600 hover:to-indigo-700 transition-all duration-300"-->
<!--          >-->
<!--            开始下载-->
<!--          </button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->

  <!-- 下载配置弹窗 -->
  <div v-if="showDownloadModal" class="fixed inset-0 z-50 flex items-center justify-center">
    <!-- 背景遮罩 -->
    <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm" @click="closeDownloadModal"></div>

    <!-- 弹窗内容 - 液态玻璃风格 -->
    <div class="relative w-full max-w-md mx-4 bg-gradient-to-br from-white/20 to-white/10 backdrop-blur-xl border border-white/30 rounded-2xl shadow-2xl overflow-hidden">
      <div class="relative p-6 z-10">
        <!-- 装饰元素 -->
        <div class="absolute -top-24 -left-24 w-48 h-48 bg-cyan-400/20 rounded-full blur-3xl"></div>
        <div class="absolute -bottom-20 -right-20 w-56 h-56 bg-blue-500/20 rounded-full blur-3xl"></div>

        <h3 class="text-xl font-semibold text-slate-800 mb-4">下载设置</h3>

        <!-- 标签页切换 -->
        <div class="flex border-b border-white/30 mb-6">
          <button
              v-for="(tab, index) in downloadTabs"
              :key="index"
              @click="selectDownloadTab(index)"
              class="px-4 py-2 font-medium text-sm transition-all duration-300"
              :class="downloadActiveTab === index ?
          'border-b-2 border-blue-500 text-blue-600' :
          'text-slate-600 hover:text-blue-500'"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 全本下载 -->
        <div v-if="downloadActiveTab === 0" class="space-y-4">
          <p class="text-slate-700">将下载《{{ selectedNovel.title }}》的全部章节</p>
        </div>

        <!-- 指定章节下载 -->
        <div v-else-if="downloadActiveTab === 1" class="space-y-4">
          <div class="flex space-x-4">
            <div class="w-1/2">
              <label class="block text-sm text-slate-600 mb-1">开始章节</label>
              <input
                  v-model="downloadConfig.startChapter"
                  type="number"
                  min="1"
                  class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/40 transition-all"
                  placeholder="开始章节"
              />
            </div>
            <div class="w-1/2">
              <label class="block text-sm text-slate-600 mb-1">结束章节</label>
              <input
                  v-model="downloadConfig.endChapter"
                  type="number"
                  min="1"
                  class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/40 transition-all"
                  placeholder="结束章节"
              />
            </div>
          </div>
        </div>

        <!-- 最新章节下载 -->
        <div v-else-if="downloadActiveTab === 2" class="space-y-4">
          <div>
            <label class="block text-sm text-slate-600 mb-1">下载最新章节数量</label>
            <input
                v-model="downloadConfig.latestChapterCount"
                type="number"
                min="1"
                class="w-full px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg border border-white/40 focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:bg-white/40 transition-all"
                placeholder="例如: 10"
            />
          </div>
        </div>

        <!-- 按钮区域 -->
        <div class="flex justify-end space-x-3 mt-6">
          <button
              @click="closeDownloadModal"
              class="px-4 py-2 rounded-lg bg-white/30 backdrop-blur-sm text-slate-700 hover:bg-white/40 transition-all duration-300 border border-white/20"
          >
            取消
          </button>

          <button
              @click="confirmDownload"
              class="px-4 py-2 rounded-lg bg-gradient-to-r from-blue-400 to-cyan-500 text-white hover:from-blue-500 hover:to-cyan-600 transition-all duration-300 shadow-lg shadow-blue-500/20 z-20 relative"
          >
            开始下载
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-item.fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.download-message {
  transition: all 0.3s ease;
}

.download-message.fade-in {
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 液态玻璃增强 */
@supports (backdrop-filter: blur(20px)) {
  .backdrop-blur-xl {
    backdrop-filter: blur(20px);
  }
  .backdrop-blur-lg {
    backdrop-filter: blur(12px);
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



