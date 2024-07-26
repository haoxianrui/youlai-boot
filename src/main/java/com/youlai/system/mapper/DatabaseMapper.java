package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.bo.ColumnMetaData;
import com.youlai.system.model.bo.TableMetaData;
import com.youlai.system.model.entity.SysDept;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 数据库 Mapper 接口
 *
 * @author ray
 * @since 2.10.0
 */
@Mapper
public interface DatabaseMapper extends BaseMapper<SysDept> {


    Page<TableMetaData> getTablePage(Page<TableMetaData> page, TablePageQuery queryParams);

    List<ColumnMetaData> getTableColumns(String tableName);


    TableMetaData getTableMetadata(String tableName);
}
