package com.youlai.boot.platform.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.generator.model.bo.ColumnMetaData;
import com.youlai.boot.platform.generator.model.bo.TableMetaData;
import com.youlai.boot.platform.generator.model.query.TablePageQuery;
import com.youlai.boot.platform.generator.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DatabaseMapper extends BaseMapper {


    Page<TablePageVO> getTablePage(Page<TablePageVO> page, TablePageQuery queryParams);

    List<ColumnMetaData> getTableColumns(String tableName);

    TableMetaData getTableMetadata(String tableName);
}
