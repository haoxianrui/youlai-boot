package com.youlai.system.listener;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.youlai.system.framework.easyexcel.ExcelResult;
import com.youlai.system.framework.easyexcel.MyAnalysisEventListener;
import com.youlai.system.pojo.vo.UserImportVO;
import com.youlai.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 用户导入监听器
 * <p>
 * https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
 *
 * @author haoxr
 * @date 2022/4/10 20:49
 */
@Slf4j
public class UserImportListener extends MyAnalysisEventListener<UserImportVO> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<UserImportVO> cachedUserList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);


    /**
     * 部门ID
     */
    private final Long deptId;

    private final SysUserService userService;

    public UserImportListener(Long deptId) {
        this.deptId = deptId;
        this.userService = SpringUtil.getBean(SysUserService.class);
    }


    /**
     * 每一条数据解析都会来调用
     *
     * @param userImportVO  一行数据，类似于 {@link AnalysisContext#readRowHolder()}
     * @param analysisContext
     */
    @Override
    public void invoke(UserImportVO userImportVO, AnalysisContext analysisContext) {
        log.info("解析到一条用户数据:{}", JSONUtil.toJsonStr(userImportVO));




    }


    /**
     * 所有数据解析完成会来调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public ExcelResult<UserImportVO> getResult() {
        return new ExcelResult<UserImportVO>() {
            @Override
            public List<UserImportVO> getList() {
                return null;
            }

            @Override
            public String getMsg() {
                return null;
            }
        };
    }
}
