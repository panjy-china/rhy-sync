package cn.Rhysync.musicserver.service.impl;

import cn.Rhysync.musicserver.domain.entity.SongList;
import cn.Rhysync.musicserver.dao.SongListDao;
import cn.Rhysync.musicserver.domain.entity.Vedio;
import cn.Rhysync.musicserver.mapper.SongListMapper;
import cn.Rhysync.musicserver.mapper.VedioMapper;
import cn.Rhysync.musicserver.service.SongListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * 歌单(SongList)表服务实现类
 *
 * @author makejava
 * @since 2025-06-22 08:43:48
 */
@Service("songListService")
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

}
