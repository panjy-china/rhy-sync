package cn.kgc.musicserver.service.impl;

import cn.kgc.musicserver.domain.entity.Singer;
import cn.kgc.musicserver.mapper.SingerMapper;
import cn.kgc.musicserver.service.ISingerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌手 服务实现类
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {

}
