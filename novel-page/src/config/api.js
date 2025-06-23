// API 基础配置
// const protocolUrl = window.location.protocol === 'https:' ? 'https://novel.x.com/api' : 'http://127.0.0.1:30000/api';

const protocolUrl = `${window.location.protocol}//${window.location.host}/api`;

// console.log('API_BASE_URL:', protocolUrl);

export const API_BASE_URL = `${protocolUrl}`;

// WebSocket 配置
// const websocketProtocolUrl = window.location.protocol === 'https:' ? 'wss://novel.x.com/ws' : 'ws://127.0.0.1:30000/ws';
const websocketProtocolUrl = window.location.protocol === 'https:' ? `wss://${window.location.host}/ws` : `ws://${window.location.host}/ws`;


// console.log('websocketProtocolUrl:', websocketProtocolUrl);
export const WS_URL = `${websocketProtocolUrl}`;

// API 路径配置
export const API_URLS = {
  // 小说相关
  NOVEL: {
    LIST: '/novel/list',
    DELETE: (id) => `/novel/delete/${id}`,
    DOWNLOAD: (id) => `/download/${id}`,
  },
  // 配置相关
  CONFIG: {
    GET: '/config/getConfig',
    GET_SOURCE_INFO: '/config/getSourceInfo',
    UPDATE: '/config/updateConfig',
  }
}

// 创建完整的 API URL
export const getFullUrl = (path) => `${API_BASE_URL}${path}` 