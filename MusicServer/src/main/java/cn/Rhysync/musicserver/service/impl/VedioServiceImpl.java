package cn.Rhysync.musicserver.service.impl;

import cn.Rhysync.musicserver.domain.entity.Vedio;
import cn.Rhysync.musicserver.mapper.VedioMapper;
import cn.Rhysync.musicserver.service.IVedioService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * (Vedio)表服务实现类
 *
 * @author makejava
 * @since 2025-06-21 19:31:34
 */
@Service("vedioService")
public class VedioServiceImpl extends ServiceImpl<VedioMapper, Vedio> implements IVedioService {
}
