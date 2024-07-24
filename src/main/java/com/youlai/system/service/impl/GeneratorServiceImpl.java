package com.youlai.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.mapper.DatabaseMapper;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.TableGeneratePreviewVO;
import com.youlai.system.model.vo.TablePageVO;
import com.youlai.system.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;

import java.io.File;
import java.util.*;

/**
 * 数据库服务实现类
 *
 * @author Ray
 * @since 2.11.0
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    private final DatabaseMapper databaseMapper;

    /**
     * 数据表分页列表
     *
     * @param queryParams 查询参数
     * @return 分页结果
     */
    public Page<TablePageVO> getTablePage(TablePageQuery queryParams) {
        Page<TablePageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 获取数据表字段列表
     *
     * @param tableName 表名
     * @return 字段列表
     */
    @Override
    public List<TableColumnVO> getTableColumns(String tableName) {
        return databaseMapper.getTableColumns(tableName);
    }

    /**
     * 获取预览生成代码
     *
     * @param tableName 表名
     * @return 预览数据
     */
    @Override
    public List<TableGeneratePreviewVO> getTablePreviewData(String tableName) {

        List<TableGeneratePreviewVO> list = new ArrayList<>();

        TemplateConfig templateConfig = new TemplateConfig("templates" , ResourceMode.CLASSPATH);
        TemplateEngine templateEngine = TemplateUtil.createEngine(templateConfig);


        Map<String, Object> bindingMap = new HashMap<>();
        bindingMap.put("tableName", "sys_user");
        bindingMap.put("author", "Ray");
        bindingMap.put("date", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
        bindingMap.put("entityName", "User" );
        bindingMap.put("lowerFirstEntityName", "user");
        bindingMap.put("tableComment", "用户");

        // 包路径
        bindingMap.put("package", "com.youlai.system");

        Template template = templateEngine.getTemplate("generator" + File.separator + "controller.java.vm");
        String content = template.render(bindingMap);
        TableGeneratePreviewVO controller = new TableGeneratePreviewVO();
        controller.setPath("youlai-boot/controller");
        controller.setContent(content);
        controller.setFileName("UserController.java");

        list.add(controller);

        TableGeneratePreviewVO vo = new TableGeneratePreviewVO();
        vo.setPath("youlai-boot/model/vo");
        vo.setContent(content);
        vo.setFileName("UserVO.java");

        list.add(vo);

        return list;
    }


    private String  generatePath(){

    }



}
