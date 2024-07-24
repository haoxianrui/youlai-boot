package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysDept;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface DatabaseMapper extends BaseMapper<SysDept> {


    Page<TablePageVO> getTablePage(Page<TablePageVO> page, TablePageQuery queryParams);

    List<TableColumnVO> getTableColumns(String tableName);


}
