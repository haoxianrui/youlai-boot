package com.youlai.boot.infrastructure.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.ColumnMetaData;
import com.youlai.boot.system.model.bo.TableMetaData;
import com.youlai.boot.system.model.entity.SysDept;
import com.youlai.boot.system.model.query.TablePageQuery;
import com.youlai.boot.system.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DatabaseMapper extends BaseMapper<SysDept> {


    Page<TablePageVO> getTablePage(Page<TablePageVO> page, TablePageQuery queryParams);

    List<ColumnMetaData> getTableColumns(String tableName);


    TableMetaData getTableMetadata(String tableName);
}
