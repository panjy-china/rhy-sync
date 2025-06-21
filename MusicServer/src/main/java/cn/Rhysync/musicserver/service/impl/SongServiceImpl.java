package cn.Rhysync.musicserver.service.impl;

import cn.Rhysync.musicserver.domain.entity.Song;
import cn.Rhysync.musicserver.mapper.SongMapper;
import cn.Rhysync.musicserver.service.ISongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲 服务实现类
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {

}
