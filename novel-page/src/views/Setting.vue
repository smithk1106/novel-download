<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
    <div class="container mx-auto px-4 py-16">
      <!-- 页面标题 -->
      <div class="mb-12 text-center">
        <h1 class="text-4xl font-bold text-gray-900">系统设置</h1>
        <p class="mt-2 text-gray-500">自定义您的下载和爬取参数</p>
      </div>

      <!-- 设置表单 -->
      <div class="max-w-3xl mx-auto glass-card mb-8">
        <form @submit.prevent="saveConfig" class="space-y-8">
          <!-- 基础设置 -->
          <div class="space-y-6">
            <h2 class="card-title">基础设置</h2>
            <div class="settings-grid">
              <div class="form-group">
                <label class="form-label">版本</label>
                <div class="glass-input disabled">
                  <input v-model="config.version" type="text" disabled />
                </div>
              </div>
              <div class="form-group relative" id="source-multiselect">
                <label class="form-label">书源ID（多选）</label>
                <!-- 选中项展示区 -->
                <div
                  class="flex flex-wrap items-center min-h-[20px] glass-input glass-multiselect px-2 py-1 cursor-pointer transition-all duration-200"
                  @click="dropdownOpen = !dropdownOpen"
                  :class="{'ring-2 ring-blue-300': dropdownOpen}"
                >
                  <template v-if="selectedSourceIds.length">
                    <span
                      v-for="id in selectedSourceIds"
                      :key="id"
                      class="glass-tag-read flex items-center mr-2 mb-2"
                    >
                      <span class="mr-1">{{ id }}</span>
                      <span
                        class="ml-1 cursor-pointer hover:text-gray-500 text-base font-bold"
                        @click.stop="removeId(id)"
                        title="移除"
                      >×</span>
                    </span>
                  </template>
                  <span v-else class="text-gray-400">请选择书源ID</span>
                  <span class="ml-auto text-gray-400">
                    <svg class="w-4 h-4 inline" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                    </svg>
                  </span>
                </div>
                <!-- 下拉选项区 -->
                <div
                  v-if="dropdownOpen"
                  class="absolute z-20 mt-1 w-full glass-dropdown border rounded-xl shadow-2xl max-h-60 overflow-auto backdrop-blur-lg bg-white/60"
                  @mousedown.stop
                >
                  <div
                    class="px-3 py-2 hover:bg-blue-100/60 cursor-pointer font-bold text-blue-600 rounded-t-xl transition-all"
                    @click.stop="toggleSelectAll"
                  >
                    {{ isAllSelected ? '取消全选' : '全选' }}
                  </div>
                  <div
                    v-for="source in sourceInfo"
                    :key="source.id"
                    class="px-3 py-2 hover:bg-blue-100/60 cursor-pointer flex items-center transition-all select-none"
                    @click.stop="toggleId(source.id)"
                  >
                    <input
                      type="checkbox"
                      class="mr-2 accent-blue-500 rounded focus:ring-2 focus:ring-blue-300"
                      :value="String(source.id)"
                      v-model="selectedSourceIds"
                      @click.stop
                    />
                    <span>{{ source.id }}</span>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">下载路径</label>
                <div class="glass-input">
                  <input v-model="config.downloadPath" type="text" />
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">文件格式</label>
                <div class="glass-select">
                  <select v-model="config.extName">
                    <option value="epub">EPUB</option>
                    <option value="txt">TXT</option>
                    <option value="html">HTML</option>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">搜索结果限制</label>
                <div class="glass-input">
                  <input
                      v-model="config.searchLimit"
                      type="number"
                      placeholder="留空显示全部"
                  />
                </div>
                <p class="form-hint">每个书源显示的搜索结果数量</p>
              </div>
              <div class="form-group">
                <label class="form-label">显示下载日志</label>
                <div class="glass-radio-group">
                  <label class="glass-radio">
                    <input type="radio" v-model="config.showDownloadLog" :value="1" />
                    <span class="radio-text">开启</span>
                  </label>
                  <label class="glass-radio">
                    <input type="radio" v-model="config.showDownloadLog" :value="0" />
                    <span class="radio-text">关闭</span>
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">保留章节缓存</label>
                <div class="glass-radio-group">
                  <label class="glass-radio">
                    <input type="radio" v-model="config.preserveChapterCache" :value="1" />
                    <span class="radio-text">是</span>
                  </label>
                  <label class="glass-radio">
                    <input type="radio" v-model="config.preserveChapterCache" :value="0" />
                    <span class="radio-text">否</span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          <!-- 爬取设置 -->
          <div class="space-y-6">
            <h2 class="card-title">爬取设置</h2>
            <div class="settings-grid">
              <div class="form-group">
                <label class="form-label">自动更新</label>
                <div class="glass-radio-group">
                  <label class="glass-radio">
                    <input type="radio" v-model="config.autoUpdate" :value="1" />
                    <span class="radio-text">开启</span>
                  </label>
                  <label class="glass-radio">
                    <input type="radio" v-model="config.autoUpdate" :value="0" />
                    <span class="radio-text">关闭</span>
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">交互模式</label>
                <div class="glass-select">
                  <select v-model="config.interactiveMode">
                    <option :value="1">模式1</option>
                    <option :value="2">模式2</option>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">爬取线程数</label>
                <div class="glass-input">
                  <input v-model="config.threads" type="number" />
                </div>
                <p class="form-hint">-1表示自动设置</p>
              </div>
              <div class="form-group">
                <label class="form-label">爬取间隔 (毫秒)</label>
                <div class="flex items-center space-x-2">
                  <div class="glass-input flex-1">
                    <input v-model="config.minInterval" type="number" placeholder="最小" />
                  </div>
                  <span>-</span>
                  <div class="glass-input flex-1">
                    <input v-model="config.maxInterval" type="number" placeholder="最大" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 重试设置 -->
          <div class="space-y-6">
            <h2 class="card-title">重试设置</h2>
            <div class="settings-grid">
              <div class="form-group">
                <label class="form-label">最大重试次数</label>
                <div class="glass-input">
                  <input v-model="config.maxRetryAttempts" type="number" />
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">重试间隔 (毫秒)</label>
                <div class="flex items-center space-x-2">
                  <div class="glass-input flex-1">
                    <input v-model="config.retryMinInterval" type="number" placeholder="最小" />
                  </div>
                  <span>-</span>
                  <div class="glass-input flex-1">
                    <input v-model="config.retryMaxInterval" type="number" placeholder="最大" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 代理设置 -->
          <div class="space-y-6">
            <h2 class="card-title">代理设置</h2>
            <div class="settings-grid">
              <div class="form-group">
                <label class="form-label">HTTP代理</label>
                <div class="glass-radio-group">
                  <label class="glass-radio">
                    <input type="radio" v-model="config.proxyEnabled" :value="1" />
                    <span class="radio-text">开启</span>
                  </label>
                  <label class="glass-radio">
                    <input type="radio" v-model="config.proxyEnabled" :value="0" />
                    <span class="radio-text">关闭</span>
                  </label>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">代理主机</label>
                <div class="glass-input" :class="{'disabled': !config.proxyEnabled}">
                  <input v-model="config.proxyHost" type="text" :disabled="!config.proxyEnabled" />
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">代理端口</label>
                <div class="glass-input" :class="{'disabled': !config.proxyEnabled}">
                  <input v-model="config.proxyPort" type="number" :disabled="!config.proxyEnabled" />
                </div>
              </div>
            </div>
          </div>

          <!-- 保存按钮 -->
          <div class="form-actions">
            <button
                type="submit"
                class="glass-button primary"
                :disabled="isSaving"
            >
              <svg v-if="isSaving" class="spinner" viewBox="0 0 24 24">
                <circle class="spinner-path" cx="12" cy="12" r="10" />
              </svg>
              <span v-else>保存设置</span>
            </button>
          </div>
        </form>
      </div>

      <!-- 数据源信息表格 -->
      <div class="max-w-3xl mx-auto glass-card">
        <h2 class="card-title">数据源信息</h2>

        <div class="overflow-x-auto bg-white bg-opacity-30 rounded-xl">
          <table class="min-w-full">
            <thead>
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                书源 ID
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                书源名称
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                大陆 IP
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                非大陆 IP
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                支持搜索
              </th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-600 uppercase tracking-wider border-b border-gray-200">
                需要注意
              </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="source in sourceInfo" :key="source.id" class="hover:bg-white hover:bg-opacity-20 transition-colors">
              <td class="px-4 py-3 text-sm text-gray-700">{{ source.id }}</td>
              <td class="px-4 py-3 text-sm text-gray-700">{{ source.name }}</td>
              <td class="px-4 py-3">
                  <span :class="getStatusClass(source.mainlandIp)">
                    {{ getStatusText(source.mainlandIp) }}
                  </span>
              </td>
              <td class="px-4 py-3">
                  <span :class="getStatusClass(source.nonMainlandIp)">
                    {{ getStatusText(source.nonMainlandIp) }}
                  </span>
              </td>
              <td class="px-4 py-3">
                  <span :class="getStatusClass(source.supportSearch)">
                    {{ getStatusText(source.supportSearch) }}
                  </span>
              </td>
              <td class="px-4 py-3 text-sm text-gray-500 max-w-xs">
                {{ source.note || '-' }}
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 添加提示弹窗 -->
      <div
          v-if="toast.show"
          class="fixed bottom-4 right-4 glass-toast flex items-center p-4 space-x-4 text-sm rounded-2xl shadow-lg transition-all duration-300"
          :class="{
          'bg-green-50 bg-opacity-80 text-green-800': toast.type === 'success',
          'bg-red-50 bg-opacity-80 text-red-800': toast.type === 'error'
        }"
      >
        <!-- 成功图标 -->
        <svg
            v-if="toast.type === 'success'"
            class="w-5 h-5 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
        >
          <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
          />
        </svg>
        <!-- 错误图标 -->
        <svg
            v-else
            class="w-5 h-5 text-red-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
        >
          <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
          />
        </svg>
        <p>{{ toast.message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, onUnmounted } from 'vue'
import { API_URLS, getFullUrl } from '@/config/api'

const config = ref({})
const sourceInfo = ref([])
const isSaving = ref(false)
const toast = ref({
  show: false,
  type: 'success',
  message: ''
})
const selectedSourceIds = ref([])
const dropdownOpen = ref(false)

// 解析 manySourceId 字符串为数组
const parseManySourceId = (str) => {
  if (!str) return []
  return str.split(',').map(s => s.trim()).filter(Boolean)
}

// 组装数组为字符串
const joinManySourceId = (arr) => {
  return arr.filter(Boolean).join(',')
}

// 显示提示
const showToast = (message, type = 'success') => {
  toast.value = {
    show: true,
    type,
    message
  }
  // 3秒后自动关闭
  setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

// 获取配置
const fetchConfig = async () => {
  try {
    isSaving.value = true
    const response = await fetch(getFullUrl(API_URLS.CONFIG.GET))
    const result = await response.json()
    if (result.code === 200) {
      config.value = result.data
      selectedSourceIds.value = parseManySourceId(config.value.manySourceId)
    } else {
      showToast(result.msg || '获取配置失败', 'error')
    }

    const responseSourceInfo = await fetch(getFullUrl(API_URLS.CONFIG.GET_SOURCE_INFO))
    const resultSourceInfo = await responseSourceInfo.json()
    if (resultSourceInfo.code === 200) {
      sourceInfo.value = resultSourceInfo.data
    } else {
      showToast(resultSourceInfo.msg || '获取数据源信息失败', 'error')
    }
  } catch (error) {
    console.error('获取配置失败:', error)
    showToast('获取配置失败，请检查网络连接', 'error')
  } finally {
    isSaving.value = false
  }
}

// 保存配置
const saveConfig = async () => {
  try {
    // 校验选中的 id 是否都在 sourceInfo 里
    const allValid = selectedSourceIds.value.every(id =>
      sourceInfo.value.some(source => String(source.id) === String(id))
    )
    if (!allValid) {
      showToast('存在无效的数据源ID', 'error')
      return
    }
    config.value.manySourceId = joinManySourceId(selectedSourceIds.value)
    isSaving.value = true
    const response = await fetch(getFullUrl(API_URLS.CONFIG.UPDATE), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(config.value)
    })
    const result = await response.json()
    if (result.code === 200) {
      showToast('保存成功')
    } else {
      showToast(result.msg || '保存失败', 'error')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    showToast('保存失败，请重试', 'error')
  } finally {
    isSaving.value = false
  }
}

// 移除单个id
const removeId = (id) => {
  selectedSourceIds.value = selectedSourceIds.value.filter(i => i !== String(id))
}

// 切换单个id选中
const toggleId = (id) => {
  id = String(id)
  if (selectedSourceIds.value.includes(id)) {
    selectedSourceIds.value = selectedSourceIds.value.filter(i => i !== id)
  } else {
    selectedSourceIds.value.push(id)
  }
}

// 判断是否全选
const isAllSelected = computed(() =>
  sourceInfo.value.length > 0 &&
  selectedSourceIds.value.length === sourceInfo.value.length
)

// 一键全选/全不选
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedSourceIds.value = []
  } else {
    selectedSourceIds.value = sourceInfo.value.map(s => String(s.id))
  }
}

// 点击外部关闭下拉
const handleClickOutside = (event) => {
  const dropdown = document.getElementById('source-multiselect')
  if (dropdown && !dropdown.contains(event.target)) {
    dropdownOpen.value = false
  }
}

onMounted(() => {
  fetchConfig()
  document.addEventListener('mousedown', handleClickOutside)
})
onUnmounted(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})

// 状态样式处理
const getStatusClass = (status) => {
  if (status === null) return 'inline-flex px-2 py-1 text-xs rounded-full bg-gray-100 bg-opacity-70 text-gray-800'
  return status
      ? 'inline-flex px-2 py-1 text-xs rounded-full bg-green-100 bg-opacity-70 text-green-800'
      : 'inline-flex px-2 py-1 text-xs rounded-full bg-red-100 bg-opacity-70 text-red-800'
}

// 状态文本处理
const getStatusText = (status) => {
  if (status === null) return '未知'
  return status ? '支持' : '不支持'
}
</script>

<style scoped>
/* 页面背景 */
.min-h-screen {
  background-attachment: fixed;
  position: relative;
}

.min-h-screen::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 70%),
  radial-gradient(circle at 20% 80%, rgba(208, 231, 255, 0.2) 0%, rgba(208, 231, 255, 0) 70%);
  z-index: -1;
}

/* 玻璃态容器 */
.glass-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 24px;
  padding: 30px;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow:
      0 10px 30px rgba(0, 0, 0, 0.05),
      0 1px 4px rgba(0, 0, 0, 0.03),
      inset 0 0 0 1px rgba(255, 255, 255, 0.4);
  position: relative;
  overflow: hidden;
}

.glass-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  position: relative;
}

/* 设置网格 */
.settings-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 24px;
}

@media (min-width: 768px) {
  .settings-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 表单组件 */
.form-group {
  margin-bottom: 6px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #3b3b3b;
  margin-bottom: 8px;
}

.form-hint {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

/* 玻璃输入框 */
.glass-input, .glass-select {
  background: rgba(255, 255, 255, 0.4);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.glass-input input, .glass-select select {
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  color: #333;
  outline: none;
  -webkit-appearance: none;
}

.glass-input:focus-within, .glass-select:focus-within {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
  border-color: rgba(59, 130, 246, 0.5);
}

.glass-input.disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 单选按钮组 */
.glass-radio-group {
  display: flex;
  gap: 16px;
}

.glass-radio {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.glass-radio:hover {
  background: rgba(255, 255, 255, 0.6);
}

.glass-radio input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.glass-radio input:checked + .radio-text {
  color: #1e88e5;
  font-weight: 500;
}

.glass-radio input:checked ~ .glass-radio {
  border-color: rgba(59, 130, 246, 0.5);
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.3);
}

.radio-text {
  font-size: 14px;
  color: #333;
}

/* 按钮 */
.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.glass-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: #333;
  transition: all 0.2s ease;
  cursor: pointer;
  min-width: 120px;
}

.glass-button:hover {
  background: rgba(255, 255, 255, 0.7);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.glass-button.primary {
  background: rgba(59, 130, 246, 0.8);
  color: white;
}

.glass-button.primary:hover {
  background: rgba(37, 99, 235, 0.9);
}

.glass-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 表格样式 */
.overflow-x-auto {
  border-radius: 16px;
  scrollbar-width: thin;
  scrollbar-color: #CBD5E0 #EDF2F7;
}

.overflow-x-auto::-webkit-scrollbar {
  height: 6px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: rgba(237, 242, 247, 0.5);
  border-radius: 3px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background-color: rgba(203, 213, 224, 0.7);
  border-radius: 3px;
}

/* 气泡提示 */
.glass-toast {
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  animation: slideIn 0.3s ease-out;
  border: 1px solid rgba(255, 255, 255, 0.7);
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 加载动画 */
.spinner {
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

.spinner-path {
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-dasharray: 60, 100;
  stroke-linecap: round;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.glass-multiselect {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.8);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04), 0 1.5px 4px rgba(0,0,0,0.03), inset 0 0 0 1px rgba(255,255,255,0.4);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition: box-shadow 0.2s, border-color 0.2s;
}
.glass-dropdown {
  background: rgba(255,255,255,0.7);
  border: 1px solid rgba(255,255,255,0.8);
  box-shadow: 0 8px 32px rgba(0,0,0,0.10), 0 1.5px 4px rgba(0,0,0,0.03), inset 0 0 0 1px rgba(255,255,255,0.4);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}
.glass-tag-read {
  background: rgba(240, 245, 255, 0.85);
  border-radius: 10px;
  border: 1px solid rgba(180,200,230,0.18);
  box-shadow: 0 1px 2px rgba(180,200,230,0.04);
  color: #34495e;
  padding: 2px 8px 2px 8px;
  margin-right: 4px;
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.2px;
  transition: background 0.2s, color 0.2s;
  line-height: 1.4;
}
.glass-tag-read:hover {
  background: rgba(200,220,255,0.18);
  color: #222;
}
</style>



