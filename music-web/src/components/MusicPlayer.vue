<script setup>
import { ref, computed, watch, onMounted } from 'vue';

const props = defineProps({
  songs: {
    type: Array,
    required: true,
    default: () => [],
    validator: (value) => {
      return value.every(song => {
        return song.name && song.url;
      });
    }
  },
  singers: {
    type: Object,
    default: () => ({})
  },
  initialVolume: {
    type: Number,
    default: 0.7,
    validator: (value) => value >= 0 && value <= 1
  },
  autoPlay: {
    type: Boolean,
    default: false
  }
});

const audioPlayer = ref(null);
const lyricsContainer = ref(null);

// 播放器状态
const isPlaying = ref(false);
const isMuted = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(props.initialVolume);
const currentSongIndex = ref(0);
const parsedLyrics = ref([]);
const currentLyricIndex = ref(-1);
const showLyrics = ref(false);
const isVisible = ref(false);

// 当前歌曲
const currentSong = computed(() => {
  return props.songs[currentSongIndex.value] || {};
});

// 歌手名称
const singerName = computed(() => {
  return props.singers[currentSong.value.singerId] || '未知歌手';
});

// 进度百分比
const progressPercentage = computed(() => {
  return (currentTime.value / duration.value) * 100 || 0;
});

// 加载歌词
const loadLyrics = () => {
  if (!currentSong.value.lyric) {
    parsedLyrics.value = [];
    return;
  }

  try {
    const lrcText = currentSong.value.lyric;
    const lines = lrcText.split('\n');
    const lyricData = [];

    lines.forEach(line => {
      // 解析LRC格式 [mm:ss.xx] 歌词文本
      const timeMatch = line.match(/\[(\d+):(\d+)\.(\d+)\]/);
      if (timeMatch) {
        const minutes = parseInt(timeMatch[1]);
        const seconds = parseInt(timeMatch[2]);
        const hundredths = parseInt(timeMatch[3]);
        const time = minutes * 60 + seconds + hundredths / 100;
        
        const text = line.replace(timeMatch[0], '').trim();
        if (text) {
          lyricData.push({ time, text });
        }
      }
      // 支持不带时间的纯文本歌词
      else if (line.trim()) {
        lyricData.push({ time: lyricData.length, text: line.trim() });
      }
    });

    parsedLyrics.value = lyricData.sort((a, b) => a.time - b.time);
  } catch (error) {
    console.error('歌词解析失败:', error);
    parsedLyrics.value = [];
  }
};

// 更新当前显示的歌词
const updateCurrentLyric = () => {
  if (parsedLyrics.value.length === 0) return;
  
  for (let i = 0; i < parsedLyrics.value.length; i++) {
    if (currentTime.value < parsedLyrics.value[i].time) {
      const newIndex = i - 1;
      if (newIndex !== currentLyricIndex.value && newIndex >= 0) {
        currentLyricIndex.value = newIndex;
        
        // 滚动到当前歌词
        if (lyricsContainer.value && showLyrics.value) {
          const activeLine = lyricsContainer.value.querySelector('.lyric-line.active');
          if (activeLine) {
            activeLine.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      }
      break;
    }
  }
};

// 播放/暂停
const togglePlay = () => {
  if (isPlaying.value) {
    audioPlayer.value.pause();
  } else {
    audioPlayer.value.play().catch(e => {
      console.error('播放失败:', e);
    });
  }
  isPlaying.value = !isPlaying.value;
  isVisible.value = true;
};

// 上一首
const prevSong = () => {
  currentSongIndex.value = (currentSongIndex.value - 1 + props.songs.length) % props.songs.length;
};

// 下一首
const nextSong = () => {
  currentSongIndex.value = (currentSongIndex.value + 1) % props.songs.length;
};
// 新增：是否是新添加的歌曲
const isNewSongAdded = ref(false);
// 加载新歌曲
const loadNewSong = () => {
  if (props.songs.length === 0) {
    audioPlayer.value.src = '';
    return;
  }
  
  // 确保索引在有效范围内
  if (currentSongIndex.value >= props.songs.length) {
    currentSongIndex.value = props.songs.length - 1;
  } else if (currentSongIndex.value < 0) {
    currentSongIndex.value = 0;
  }

  isPlaying.value = false;
  audioPlayer.value.src = currentSong.value.url;
  loadLyrics();
  
  if (props.autoPlay || isNewSongAdded.value) {
    isNewSongAdded.value = false;
    setTimeout(() => {
      togglePlay();
    }, 100);
  }
};


// 静音/取消静音
const toggleMute = () => {
  isMuted.value = !isMuted.value;
  audioPlayer.value.muted = isMuted.value;
};

// 设置音量
const setVolume = (e) => {
  const volumeBar = e.currentTarget;
  const clickPosition = e.clientX - volumeBar.getBoundingClientRect().left;
  const newVolume = clickPosition / volumeBar.clientWidth;
  volume.value = Math.max(0, Math.min(1, newVolume));
  audioPlayer.value.volume = volume.value;
  isMuted.value = false;
  audioPlayer.value.muted = false;
};

// 跳转进度
const seek = (e) => {
  const progressBar = e.currentTarget;
  const clickPosition = e.clientX - progressBar.getBoundingClientRect().left;
  const newTime = (clickPosition / progressBar.clientWidth) * duration.value;
  audioPlayer.value.currentTime = newTime;
};

// 更新时间
const updateTime = () => {
  currentTime.value = audioPlayer.value.currentTime;
  updateCurrentLyric();
};

// 更新总时长
const updateDuration = () => {
  duration.value = audioPlayer.value.duration;
};

// 歌曲结束处理
const handleSongEnd = () => {
  isPlaying.value = false;
  if (props.songs.length > 1) {
    nextSong();
  }
};

// 格式化时间 (秒 -> mm:ss)
const formatTime = (time) => {
  if (isNaN(time)) return '00:00';
  
  const minutes = Math.floor(time / 60);
  const seconds = Math.floor(time % 60);
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};

// 显示/隐藏歌词面板
const toggleLyrics = () => {
  showLyrics.value = !showLyrics.value;
};

// 监听当前歌曲变化
watch(currentSongIndex, loadNewSong);
watch(() => props.songs, (newSongs, oldSongs) => {
  // 如果是添加了新歌曲而不是删除
  if (newSongs.length > oldSongs.length) {
    isNewSongAdded.value = true;
    // 如果是第一首歌曲，直接播放
    if (oldSongs.length === 0) {
      currentSongIndex.value = 0;
    }
    // 否则保持当前播放位置
  }
  // 无论如何都重新加载当前歌曲
  loadNewSong();
}, { deep: true });
// 初始化
onMounted(() => {
  if (props.songs.length > 0) {
    loadNewSong();
  }
});
</script>

<template>
  <div class="music-player-container">
    <!-- 主播放器固定在底部 -->
    <div class="music-player" :class="{ 'player-visible': isVisible }">
      <div class="player-container">
        <!-- 歌曲信息 -->
        <div class="song-info">
          <div class="cover" :style="{ backgroundImage: `url(${currentSong.pic})` }"></div>
          <div class="info">
            <h3 class="title">{{ currentSong.name }}</h3>
            <p class="artist">{{ singerName }}</p>
          </div>
        </div>

        <!-- 播放控制 -->
        <div class="player-controls">
          <button class="control-btn" @click="prevSong">
            <i class="icon-prev"></i>
          </button>
          <button class="control-btn play-btn" @click="togglePlay">
            <i :class="isPlaying ? 'icon-pause' : 'icon-play'"></i>
          </button>
          <button class="control-btn" @click="nextSong">
            <i class="icon-next"></i>
          </button>
        </div>

        <!-- 进度条 -->
        <div class="progress-container">
          <span class="time current-time">{{ formatTime(currentTime) }}</span>
          <div class="progress-bar" @click="seek">
            <div class="progress" :style="{ width: progressPercentage + '%' }"></div>
          </div>
          <span class="time duration">{{ formatTime(duration) }}</span>
        </div>

        <!-- 音量控制 -->
        <div class="volume-control">
          <button class="control-btn" @click="toggleMute">
            <i :class="isMuted ? 'icon-volume-off' : 'icon-volume-up'"></i>
          </button>
          <div class="volume-bar" @click="setVolume">
            <div class="volume-level" :style="{ width: volume * 100 + '%' }"></div>
          </div>
        </div>

        <!-- 播放列表和歌词按钮 -->
        <div class="extra-controls">
          <button class="playlist-btn" @click="togglePlaylist">
            <i class="icon-playlist"></i>
            <span class="badge" v-if="songs.length">{{ songs.length }}</span>
          </button>
          <button class="lyrics-btn" @click="toggleLyrics">
            <i class="icon-lyrics"></i>
          </button>
        </div>
      </div>

      <!-- 歌词面板 -->
      <div class="lyrics-panel" :class="{ 'lyrics-show': showLyrics }">
        <div class="lyrics-container" ref="lyricsContainer">
          <div
            v-for="(line, index) in parsedLyrics"
            :key="index"
            class="lyric-line"
            :class="{ active: currentLyricIndex === index }"
          >
            {{ line.text }}
          </div>
          <div v-if="parsedLyrics.length === 0" class="no-lyrics">
            暂无歌词
          </div>
        </div>
      </div>
    </div>

    <!-- 播放列表面板 - 固定在右侧 -->
    <div class="playlist-panel" :class="{ 'playlist-show': showPlaylist }">
      <div class="playlist-header">
        <h3>播放列表 ({{ songs.length }})</h3>
        <button class="close-btn" @click="togglePlaylist">
          <i class="icon-close"></i>
        </button>
      </div>
      <div class="playlist-container">
        <div
          v-for="(song, index) in songs"
          :key="song.id"
          class="playlist-item"
          :class="{ active: currentSongIndex === index }"
          @click="playSong(index)"
        >
          <div class="item-cover" :style="{ backgroundImage: `url(${song.pic})` }"></div>
          <div class="item-info">
            <h4 class="item-title">{{ song.name }}</h4>
            <p class="item-artist">{{ singers[song.singerId] || '未知歌手' }}</p>
          </div>
          <div class="item-duration">{{ formatTime(song.duration || 0) }}</div>
          <button class="item-remove" @click.stop="removeSong(index)">
            <i class="icon-remove"></i>
          </button>
        </div>
        <div v-if="songs.length === 0" class="empty-playlist">
          播放列表为空
        </div>
      </div>
    </div>

    <!-- 音频元素 -->
    <audio
      ref="audioPlayer"
      @timeupdate="updateTime"
      @ended="handleSongEnd"
      @loadedmetadata="updateDuration"
    ></audio>
  </div>
</template>



<style scoped>
/* 基础布局 */
.music-player-container {
  position: relative;
}

/* 主播放器 - 固定在底部 */
.music-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background-color: #2c3e50;
  color: white;
  z-index: 1000;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.player-visible {
  transform: translateY(0);
}

.player-container {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 歌曲信息 */
.song-info {
  display: flex;
  align-items: center;
  width: 25%;
  min-width: 200px;
  overflow: hidden;
}

.cover {
  width: 50px;
  height: 50px;
  background-size: cover;
  background-position: center;
  border-radius: 4px;
  margin-right: 15px;
  flex-shrink: 0;
  background-color: #34495e;
}

.info {
  flex: 1;
  min-width: 0;
}

.title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.artist {
  margin: 3px 0 0;
  font-size: 12px;
  color: #bdc3c7;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 播放控制 */
.player-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30%;
  min-width: 200px;
}

.control-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 8px;
  margin: 0 5px;
  outline: none;
  transition: color 0.2s;
}

.control-btn:hover {
  color: #1abc9c;
}

.play-btn {
  background-color: #1abc9c;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-btn:hover {
  background-color: #16a085;
  color: white;
}

/* 进度条 */
.progress-container {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0 15px;
}

.progress-bar {
  flex-grow: 1;
  height: 4px;
  background-color: #7f8c8d;
  border-radius: 2px;
  cursor: pointer;
  margin: 0 10px;
}

.progress {
  height: 100%;
  background-color: #1abc9c;
  border-radius: 2px;
  transition: width 0.1s linear;
}

.time {
  font-size: 11px;
  color: #bdc3c7;
  min-width: 40px;
}

/* 音量控制 */
.volume-control {
  display: flex;
  align-items: center;
  width: 15%;
  min-width: 120px;
}

.volume-bar {
  flex-grow: 1;
  height: 4px;
  background-color: #7f8c8d;
  border-radius: 2px;
  cursor: pointer;
  margin-left: 10px;
}

.volume-level {
  height: 100%;
  background-color: #1abc9c;
  border-radius: 2px;
}

/* 额外控制按钮 */
.extra-controls {
  display: flex;
  align-items: center;
  margin-left: 15px;
}

.playlist-btn, .lyrics-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 8px;
  margin: 0 5px;
  outline: none;
  transition: color 0.2s;
  position: relative;
}

.playlist-btn:hover, .lyrics-btn:hover {
  color: #1abc9c;
}

.badge {
  position: absolute;
  top: 3px;
  right: 3px;
  background-color: #e74c3c;
  color: white;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 歌词面板 */
.lyrics-panel {
  position: absolute;
  bottom: 80px;
  left: 0;
  right: 0;
  height: 0;
  background-color: rgba(44, 62, 80, 0.95);
  overflow: hidden;
  transition: height 0.3s ease;
  z-index: 999;
}

.lyrics-show {
  height: 300px;
}

.lyrics-container {
  height: 100%;
  overflow-y: auto;
  padding: 20px;
  text-align: center;
}

.lyric-line {
  margin: 15px 0;
  font-size: 16px;
  color: #bdc3c7;
  transition: all 0.3s;
}

.lyric-line.active {
  color: #1abc9c;
  font-size: 18px;
  font-weight: 500;
}

.no-lyrics {
  color: #7f8c8d;
  font-size: 14px;
  margin-top: 50px;
}

/* 播放列表面板 - 固定在右侧 */
.playlist-panel {
  position: fixed;
  bottom: 80px;
  right: -400px;
  width: 350px;
  height: calc(100vh - 80px);
  background-color: rgba(44, 62, 80, 0.95);
  transition: right 0.3s ease;
  z-index: 1001;
  border-top-left-radius: 8px;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.2);
}

.playlist-show {
  right: 0;
}

.playlist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #34495e;
}

.playlist-header h3 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  color: #bdc3c7;
  font-size: 16px;
  cursor: pointer;
  padding: 5px;
}

.close-btn:hover {
  color: #1abc9c;
}

.playlist-container {
  height: calc(100% - 50px);
  overflow-y: auto;
  padding-bottom: 20px;
}

.playlist-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.playlist-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.playlist-item.active {
  background-color: rgba(26, 188, 156, 0.2);
}

.item-cover {
  width: 40px;
  height: 40px;
  background-size: cover;
  background-position: center;
  border-radius: 4px;
  margin-right: 12px;
  flex-shrink: 0;
  background-color: #34495e;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  margin: 0;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-artist {
  margin: 3px 0 0;
  font-size: 12px;
  color: #bdc3c7;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-duration {
  font-size: 12px;
  color: #bdc3c7;
  margin: 0 12px;
  flex-shrink: 0;
}

.item-remove {
  background: none;
  border: none;
  color: #bdc3c7;
  font-size: 14px;
  cursor: pointer;
  padding: 5px;
  opacity: 0;
  transition: opacity 0.2s, color 0.2s;
  flex-shrink: 0;
}

.playlist-item:hover .item-remove {
  opacity: 1;
}

.item-remove:hover {
  color: #e74c3c;
}

.empty-playlist {
  color: #7f8c8d;
  text-align: center;
  padding: 50px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .player-container {
    padding: 0 10px;
  }
  
  .song-info {
    width: auto;
    min-width: 0;
    flex: 1;
  }
  
  .player-controls {
    width: auto;
    min-width: 0;
  }
  
  .progress-container {
    display: none;
  }
  
  .volume-control {
    display: none;
  }
  
  .playlist-panel {
    width: 100%;
    right: -100%;
  }
}

/* 图标样式 */
.icon-prev::before { content: '⏮'; }
.icon-next::before { content: '⏭'; }
.icon-play::before { content: '▶'; }
.icon-pause::before { content: '⏸'; }
.icon-volume-up::before { content: '🔊'; }
.icon-volume-off::before { content: '🔇'; }
.icon-lyrics::before { content: '🎵'; }
.icon-playlist::before { content: '📋'; }
.icon-close::before { content: '✕'; }
.icon-remove::before { content: '🗑'; }
</style>