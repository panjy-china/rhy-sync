package cn.Rhysync.musicserver.service;

import cn.Rhysync.musicserver.domain.entity.SongList;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 歌单(SongList)表服务接口
 *
 * @author makejava
 * @since 2025-06-22 08:43:48
 */
public interface SongListService extends IService<SongList> {
}
