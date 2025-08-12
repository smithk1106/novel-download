// 当前页面 URL
const url = decodeURI(location.href)
const prefixUrl = url.substring(0, url.lastIndexOf('/') + 1)

// 获取当前文件名
let index = url.match(/(\d+)_.html/)[1]
const indexSize = index.length
console.log('当前页索引', index)

if (index <= 1) {
  document.getElementById('btn-pre').disabled = true
}

function turnPage(action) {
  action === 'next' ? ++index : --index
  if (index < 1) {
    index = 1
    return
  }
  location.href = prefixUrl + String(index).padStart(indexSize, '0') + '_.html'
}

// 监听键盘按下事件
document.addEventListener('keyup', function (e) {
  // 检查按下的是左箭头键还是右箭头键
  switch (e.key) {
    case 'ArrowRight':
      turnPage('next')
      break
    case 'ArrowLeft':
      turnPage('pre')
      break
    default:
      break
  }
})
