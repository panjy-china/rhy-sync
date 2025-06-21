package cn.Rhysync.musicserver.service.impl;

import cn.Rhysync.musicserver.domain.entity.Admin;
import cn.Rhysync.musicserver.mapper.AdminMapper;
import cn.Rhysync.musicserver.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员 服务实现类
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

}
