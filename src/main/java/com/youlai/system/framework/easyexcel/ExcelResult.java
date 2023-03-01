package com.youlai.system.framework.easyexcel;

import java.util.List;

/**
 * Excel 读取结果
 *
 * @author: haoxr
 * @date: 2023/03/01
 */
public interface ExcelResult<T> {

    List<T> getList();

    String getMsg();

}
