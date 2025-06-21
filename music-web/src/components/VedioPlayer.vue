<script setup>
import { ref, computed, watch, onMounted } from 'vue';

const props = defineProps({
  videos: {
    type: Array,
    required: true,
    default: () => [],
    validator: (value) => {
      return value.every(video => {
        return video.vName && video.vUrl;
      });
    }
  },
  autoPlay: {
    type: Boolean,
    default: false
  },
  initialVolume: {
    type: Number,
    default: 0.7,
    validator: (value) => value >= 0 && value <= 1
  }
});

const videoPlayer = ref(null);
const playerContainer = ref(null);

// 播放器状态
const isPlaying = ref(false);
const isMuted = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(props.initialVolume);
const currentVideoIndex = ref(0);
const isFullscreen = ref(false);
const isVisible = ref(false);
const showControls = ref(true);
let controlsTimeout = null;

// 当前视频
const currentVideo = computed(() => {
  return props.videos[currentVideoIndex.value] || {};
});

// 进度百分比
const progressPercentage = computed(() => {
  return (currentTime.value / duration.value) * 100 || 0;
});

// 加载新视频
const loadNewVideo = () => {
  if (props.videos.length === 0) {
    videoPlayer.value.src = '';
    return;
  }

  // 确保索引在有效范围内
  if (currentVideoIndex.value >= props.videos.length) {
    currentVideoIndex.value = props.videos.length - 1;
  } else if (currentVideoIndex.value < 0) {
    currentVideoIndex.value = 0;
  }

  isPlaying.value = false;
  videoPlayer.value.src = currentVideo.value.vUrl;

  if (props.autoPlay) {
    setTimeout(() => {
      togglePlay();
    }, 100);
  }
};

// 播放/暂停
const togglePlay = () => {
  if (isPlaying.value) {
    videoPlayer.value.pause();
  } else {
    videoPlayer.value.play().catch(e => {
      console.error('播放失败:', e);
    });
  }
  isPlaying.value = !isPlaying.value;
  isVisible.value = true;
};

// 上一视频
const prevVideo = () => {
  currentVideoIndex.value = (currentVideoIndex.value - 1 + props.videos.length) % props.videos.length;
};

// 下一视频
const nextVideo = () => {
  currentVideoIndex.value = (currentVideoIndex.value + 1) % props.videos.length;
};

// 静音/取消静音
const toggleMute = () => {
  isMuted.value = !isMuted.value;
  videoPlayer.value.muted = isMuted.value;
};

// 设置音量
const setVolume = (e) => {
  const volumeBar = e.currentTarget;
  const clickPosition = e.clientX - volumeBar.getBoundingClientRect().left;
  const newVolume = clickPosition / volumeBar.clientWidth;
  volume.value = Math.max(0, Math.min(1, newVolume));
  videoPlayer.value.volume = volume.value;
  isMuted.value = false;
  videoPlayer.value.muted = false;
};

// 跳转进度
const seek = (e) => {
  const progressBar = e.currentTarget;
  const clickPosition = e.clientX - progressBar.getBoundingClientRect().left;
  const newTime = (clickPosition / progressBar.clientWidth) * duration.value;
  videoPlayer.value.currentTime = newTime;
};

// 更新时间
const updateTime = () => {
  currentTime.value = videoPlayer.value.currentTime;
};

// 更新总时长
const updateDuration = () => {
  duration.value = videoPlayer.value.duration;
};

// 视频结束处理
const handleVideoEnd = () => {
  isPlaying.value = false;
  if (props.videos.length > 1) {
    nextVideo();
  }
};

// 格式化时间 (秒 -> mm:ss)
const formatTime = (time) => {
  if (isNaN(time)) return '00:00';

  const minutes = Math.floor(time / 60);
  const seconds = Math.floor(time % 60);
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};

// 切换全屏
const toggleFullscreen = () => {
  if (!isFullscreen.value) {
    playerContainer.value.requestFullscreen?.() ||
    playerContainer.value.webkitRequestFullscreen?.() ||
    playerContainer.value.msRequestFullscreen?.();
  } else {
    document.exitFullscreen?.() ||
    document.webkitExitFullscreen?.() ||
    document.msExitFullscreen?.();
  }
};

// 控制条显示/隐藏
const resetControlsTimer = () => {
  showControls.value = true;
  clearTimeout(controlsTimeout);
  controlsTimeout = setTimeout(() => {
    if (isPlaying.value) {
      showControls.value = false;
    }
  }, 3000);
};

// 监听当前视频变化
watch(currentVideoIndex, loadNewVideo);
watch(() => props.videos, loadNewVideo, { deep: true });

// 初始化
onMounted(() => {
  if (props.videos.length > 0) {
    loadNewVideo();
  }

  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement;
  });

  document.addEventListener('keydown', (e) => {
    if (e.code === 'Space') {
      e.preventDefault();
      togglePlay();
    }
  });
});
</script>

<template>
  <div
      class="video-player-container"
      ref="playerContainer"
      @mousemove="resetControlsTimer"
      @click="resetControlsTimer"
  >
    <!-- 视频元素 -->
    <video
        ref="videoPlayer"
        class="video-element"
        @timeupdate="updateTime"
        @ended="handleVideoEnd"
        @loadedmetadata="updateDuration"
        @click="togglePlay"
    ></video>

    <!-- 视频封面 -->
    <div
        v-if="!isPlaying && currentVideo.vImg"
        class="video-cover"
        :style="{ backgroundImage: `url(${currentVideo.vImg})` }"
        @click="togglePlay"
    >
      <button class="play-button">
        <i class="icon-play"></i>
      </button>
    </div>

    <!-- 控制条 -->
    <div class="video-controls" :class="{ 'controls-visible': showControls || !isPlaying }">
      <!-- 进度条 -->
      <div class="progress-container">
        <div class="progress-bar" @click="seek">
          <div class="progress" :style="{ width: progressPercentage + '%' }"></div>
        </div>
      </div>

      <div class="controls-container">
        <!-- 左侧控制区 -->
        <div class="left-controls">
          <button class="control-btn" @click="togglePlay">
            <i :class="isPlaying ? 'icon-pause' : 'icon-play'"></i>
          </button>
          <span class="time-display">
            {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
          </span>
        </div>

        <!-- 右侧控制区 -->
        <div class="right-controls">
          <button class="control-btn" @click="toggleMute">
            <i :class="isMuted ? 'icon-volume-off' : 'icon-volume-up'"></i>
          </button>
          <div class="volume-control" @click.stop="setVolume">
            <div class="volume-bar">
              <div class="volume-level" :style="{ width: volume * 100 + '%' }"></div>
            </div>
          </div>
          <button class="control-btn" @click="toggleFullscreen">
            <i :class="isFullscreen ? 'icon-fullscreen-exit' : 'icon-fullscreen'"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 视频标题 -->
    <div class="video-title" :class="{ 'title-visible': showControls || !isPlaying }">
      {{ currentVideo.vName }}
    </div>
  </div>
</template>

<style scoped>
/* 基础布局 */
.video-player-container {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #000;
  overflow: hidden;
  border-radius: 4px;
}

/* 视频元素 */
.video-element {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

/* 视频封面 */
.video-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.play-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  border: none;
  color: white;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, background-color 0.2s;
}

.play-button:hover {
  transform: scale(1.1);
  background-color: rgba(0, 0, 0, 0.8);
}

/* 控制条 */
.video-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  padding: 10px 15px;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.controls-visible {
  transform: translateY(0);
}

/* 进度条 */
.progress-container {
  width: 100%;
  margin-bottom: 8px;
}

.progress-bar {
  height: 4px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  cursor: pointer;
  position: relative;
}

.progress {
  height: 100%;
  background-color: #1abc9c;
  border-radius: 2px;
  transition: width 0.1s linear;
}

/* 控制按钮容器 */
.controls-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 控制按钮 */
.control-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 5px 8px;
  margin: 0 5px;
  outline: none;
  transition: color 0.2s;
}

.control-btn:hover {
  color: #1abc9c;
}

/* 时间显示 */
.time-display {
  color: white;
  font-size: 13px;
  margin-left: 10px;
  font-family: monospace;
}

/* 音量控制 */
.volume-control {
  display: flex;
  align-items: center;
  margin-right: 15px;
}

.volume-bar {
  width: 60px;
  height: 4px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  cursor: pointer;
  margin-left: 8px;
}

.volume-level {
  height: 100%;
  background-color: #1abc9c;
  border-radius: 2px;
}

/* 视频标题 */
.video-title {
  position: absolute;
  top: -40px;
  left: 0;
  right: 0;
  padding: 10px 15px;
  color: white;
  font-size: 14px;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.7), transparent);
  transition: top 0.3s ease;
}

.title-visible {
  top: 0;
}

/* 图标样式 */
.icon-play::before { content: '▶'; }
.icon-pause::before { content: '⏸'; }
.icon-volume-up::before { content: '🔊'; }
.icon-volume-off::before { content: '🔇'; }
.icon-fullscreen::before { content: '⛶'; }
.icon-fullscreen-exit::before { content: '🗗'; }

/* 响应式设计 */
@media (max-width: 768px) {
  .volume-control {
    display: none;
  }

  .time-display {
    font-size: 11px;
  }
}
</style>