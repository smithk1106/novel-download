<template>
  <header
    class="fixed top-0 left-0 right-0 z-50 transition-all duration-300 bg-white/60 backdrop-blur-xl border-b border-white/30 shadow-lg rounded-b-2xl"
  >
    <nav class="container mx-auto px-6">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center space-x-2 hover:opacity-80 transition-opacity logo-link">
          <svg class="w-8 h-8 text-blue-600" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 4.75L19.25 9L12 13.25L4.75 9L12 4.75Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M4.75 15L12 19.25L19.25 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="text-xl font-semibold text-gray-900">小说下载器</span>
        </router-link>

        <!-- Navigation Links -->
        <div class="hidden md:flex items-center space-x-1">
          <div v-for="(item, index) in navItems" :key="index" class="relative group">
            <router-link 
              :to="item.path" 
              class="nav-link px-4 py-2 text-gray-600 rounded-lg text-sm font-medium transition-all duration-300 hover:bg-blue-50/60"
              :class="{ 'text-blue-600': $route.path === item.path }"
            >
              {{ item.name }}
            </router-link>
          </div>
        </div>

        <!-- Mobile Menu Button -->
        <button 
          @click="isMobileMenuOpen = !isMobileMenuOpen"
          class="md:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200 ml-2"
        >
          <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path v-if="!isMobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- Mobile Menu -->
      <div 
        v-show="isMobileMenuOpen"
        class="md:hidden py-4 space-y-2"
      >
        <router-link 
          v-for="(item, index) in navItems"
          :key="index"
          :to="item.path"
          class="block px-4 py-2 text-gray-600 rounded-lg hover:bg-blue-50/60 transition-colors duration-200"
          :class="{ 'text-blue-600': $route.path === item.path }"
        >
          {{ item.name }}
        </router-link>
      </div>
    </nav>
  </header>

  <!-- Spacer to prevent content from hiding under fixed header -->
  <div class="h-16"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const isMobileMenuOpen = ref(false)
const isDark = ref(false)

const navItems = [
  {
    name: '首页',
    path: '/'
  },
  {
    name: '搜索下载',
    path: '/pages/crawler'
  },
  {
    name: '我的书库',
    path: '/pages/novels'
  },
  {
    name: '设置',
    path: '/pages/setting'
  }
]

function toggleDarkMode() {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

onMounted(() => {
  const theme = localStorage.getItem('theme')
  if (theme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  } else {
    isDark.value = false
    document.documentElement.classList.remove('dark')
  }
})
</script>

<style scoped>
.nav-link {
  position: relative;
  isolation: isolate;
}

.nav-link::before {
  content: '';
  position: absolute;
  inset: 0;
  background-color: rgb(37 99 235 / 0.08);
  border-radius: 8px;
  transform: scale(0.8);
  opacity: 0;
  transition: all 0.3s ease;
  z-index: -1;
}

.nav-link:hover {
  color: rgb(37 99 235);
}

.nav-link:hover::before {
  transform: scale(1);
  opacity: 1;
}

.nav-link.router-link-active {
  position: relative;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  width: 16px;
  height: 2px;
  background-color: rgb(37 99 235);
  transform: translateX(-50%);
  border-radius: 1px;
}

.logo-link.router-link-active::after {
  display: none;
}
</style>

