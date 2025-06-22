package cn.kgc.musicserver.service.impl;


import cn.kgc.musicserver.domain.entity.Comment;
import cn.kgc.musicserver.mapper.CommentMapper;
import cn.kgc.musicserver.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService{

}
