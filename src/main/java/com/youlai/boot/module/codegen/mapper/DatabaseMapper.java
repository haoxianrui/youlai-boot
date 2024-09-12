package com.youlai.boot.module.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.module.codegen.model.bo.ColumnMetaData;
import com.youlai.boot.module.codegen.model.bo.TableMetaData;
import com.youlai.boot.module.codegen.model.query.TablePageQuery;
import com.youlai.boot.module.codegen.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DatabaseMapper extends BaseMapper {


    Page<TablePageVO> getTablePage(Page<TablePageVO> page, TablePageQuery queryParams);

    List<ColumnMetaData> getTableColumns(String tableName);

    TableMetaData getTableMetadata(String tableName);
}
