package cn.Rhysync.musicserver.service.impl;

import cn.Rhysync.musicserver.domain.entity.Title;
import cn.Rhysync.musicserver.mapper.TitleMapper;
import cn.Rhysync.musicserver.service.ITitleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@Service
public class TitleServiceImpl extends ServiceImpl<TitleMapper, Title> implements ITitleService {

}
