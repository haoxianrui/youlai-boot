package com.youlai.system.common.util;

import com.alibaba.excel.EasyExcel;
import com.youlai.system.framework.easyexcel.ExcelResult;
import com.youlai.system.framework.easyexcel.MyAnalysisEventListener;

import java.io.InputStream;

/**
 * Excel 工具类
 *
 * @author: haoxr
 * @date: 2023/03/01
 */
public class ExcelUtils {

    public static <T> ExcelResult importExcel(InputStream is, Class clazz, MyAnalysisEventListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        ExcelResult excelResult = listener.getResult();
        return excelResult;
    }


}
