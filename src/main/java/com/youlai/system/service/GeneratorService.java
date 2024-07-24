package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.form.GenConfigForm;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.GeneratorPreviewVO;
import com.youlai.system.model.vo.TablePageVO;

import java.util.List;

/**
 * 代码生成业务接口
 *
 * @author haoxr
 * @since 2.11.0
 */
public interface GeneratorService {


    /**
     * 获取数据表分页列表
     *
     * @param queryParams 查询参数
     * @return
     */
    Page<TablePageVO> getTablePage(TablePageQuery queryParams);

    /**
     * 获取数据表字段列表
     *
     * @param tableName 表名
     * @return
     */
    List<TableColumnVO> getTableColumns(String tableName);

    /**
     * 获取预览生成代码
     *
     * @param tableName 表名
     * @return
     */
    List<GeneratorPreviewVO> getTablePreviewData(String tableName);

    /**
     * 获取代码生成配置
     *
     * @param tableName 表名
     * @return
     */
    GenConfigForm getGenConfig(String tableName);

    /**
     * 保存代码生成配置
     *
     * @param formData 表单数据
     * @return
     */
    boolean saveGenCodeConfig(GenConfigForm formData);
}
