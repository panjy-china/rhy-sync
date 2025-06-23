package cn.Rhysync.musicserver.utils;

import cn.Rhysync.musicserver.MusicServerApplication;
import cn.Rhysync.musicserver.domain.entity.Vedio;
import cn.Rhysync.musicserver.mapper.VedioMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Component
public class VedioFileImporter implements CommandLineRunner {  // 实现CommandLineRunner接口

    @Autowired
    private VedioMapper vedioMapper; // MyBatis-Plus的Mapper

    // 移除静态的vedioFileImporter引用

    private static final String BASE_DIR = "static/vedio/";
    private static final String AUTHOR = "Panjy";
    private static final int REGION_ID = 14;
    private static final int VERSION_ID = 1;

    /**
     * 读取视频和图片文件，生成Vedio列表
     */
    public List<Vedio> importVedios() throws IOException {
        List<Vedio> vedios = new ArrayList<>();
        Path dirPath = Paths.get(new ClassPathResource(BASE_DIR).getURI());

        // 遍历目录下的文件（仅处理视频文件，自动匹配同名图片）
        try (Stream<Path> paths = Files.list(dirPath)) {
            paths.filter(this::isVideoFile)
                    .forEach(videoPath -> {
                        String baseName = getBaseName(videoPath.getFileName().toString());
                        Path imgPath = dirPath.resolve(baseName + ".jpg"); // 假设图片为jpg格式

                        if (Files.exists(imgPath)) {
                            vedios.add(createVedioEntity(baseName, videoPath, imgPath));
                        }
                    });
        }
        return vedios;
    }

    /**
     * 判断是否为视频文件（按扩展名）
     */
    private boolean isVideoFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".avi");
    }

    /**
     * 获取文件名（不含扩展名）
     */
    private String getBaseName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * 创建Vedio实体
     */
    private Vedio createVedioEntity(String baseName, Path videoPath, Path imgPath) {
        return new Vedio()
                .setVName(baseName)
                .setVAuthor(AUTHOR)
                .setVPlayback(new Random().nextInt(1000)) // 0-999随机数
                .setVCreatetime(new Date(2025 - 1900, 5, 22)) // 2025/6/22
                .setVImg("/vedio/" + imgPath.getFileName().toString())
                .setVUrl("/vedio/" + videoPath.getFileName().toString())
                .setRegionid(REGION_ID)
                .setVersionid(VERSION_ID);
    }

    @Transactional
    public void importVediosToDatabase() {
        try {
            // 1. 读取文件并生成Vedio对象列表
            List<Vedio> vedios = this.importVedios();  // 使用this调用实例方法

            // 2. 批量插入数据库（不需要Wrapper）
            vedios.forEach(vedio -> {
                // 检查是否已存在同名视频（可选）
                QueryWrapper<Vedio> wrapper = new QueryWrapper<>();
                wrapper.eq("v_name", vedio.getVName());

                if (vedioMapper.selectCount(wrapper) == 0) {
                    vedioMapper.insert(vedio); // 插入新记录
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("导入视频失败: " + e.getMessage(), e);
        }
    }

    // 实现CommandLineRunner接口的run方法
    @Override
    public void run(String... args) throws Exception {
        this.importVediosToDatabase();
    }

    public static void main(String[] args) {
        // 手动启动Spring容器
        ApplicationContext context = SpringApplication.run(MusicServerApplication.class, args);

        // 从容器中获取实例
        VedioFileImporter importer = context.getBean(VedioFileImporter.class);
        importer.importVediosToDatabase();
    }
}