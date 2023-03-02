package com.youlai.system.listener;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.constant.SystemConstants;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.converter.UserConverter;
import com.youlai.system.framework.easyexcel.MyAnalysisEventListener;
import com.youlai.system.pojo.entity.SysUser;
import com.youlai.system.pojo.vo.UserImportVO;
import com.youlai.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * 用户导入监听器
 * <p>
 * 最简单的读监听器：https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
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

    private int validCount;

    private int invalidCount;

    private int currentIndex;

    StringBuilder msg = new StringBuilder();


    /**
     * 缓存的数据
     */
    private List<SysUser> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 部门ID
     */
    private final Long deptId;

    private final SysUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final UserConverter userConverter;

    public UserImportListener(Long deptId) {
        this.deptId = deptId;
        this.userService = SpringUtil.getBean(SysUserService.class);
        this.passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        this.userConverter = SpringUtil.getBean(UserConverter.class);
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
        currentIndex++;
        StringBuilder rowMsg = new StringBuilder();
        boolean rowFlag = true;
        // 校验数据

        String username = userImportVO.getUsername();
        if (StrUtil.isBlank(username)) {
            rowFlag = false;
            rowMsg.append("用户名为空；");
        } else {
            long count = userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
            if (count > 0) {
                rowFlag = false;
                rowMsg.append("用户名已存在；");
            }
        }

        String nickname = userImportVO.getNickname();
        if (StrUtil.isBlank(nickname)) {
            rowFlag = false;
            rowMsg.append("用户昵称为空；");
        }


        String mobile = userImportVO.getMobile();
        if (StrUtil.isBlank(mobile)) {
            rowFlag = false;
            rowMsg.append("手机号码为空；");
        } else {
            if (!Validator.isMobile(mobile)) {
                rowFlag = false;
                rowMsg.append("手机号码不正确；");
            }
        }

        if (rowFlag) {
            validCount++;
            SysUser entity = userConverter.importVo2Entity(userImportVO);
            // 默认密码
            entity.setPassword(passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD));
            // 性别转换
            Integer gender = (Integer) IBaseEnum.getValueByLabel(userImportVO.getGender(), GenderEnum.class);
            entity.setGender(gender);
            entity.setDeptId(deptId);

            cachedDataList.add(entity);
        } else {
            invalidCount++;
            msg.append("第" + currentIndex + "行数据校验失败：").append(rowMsg + "<br/>");
        }

        if (cachedDataList.size() > BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    /**
     * 所有数据解析完成会来调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        msg = new StringBuilder("导入用户结束：成功" + validCount + "条；失败" + invalidCount + "条<br/>").append(msg);
    }

    /**
     * 存储数据库
     */
    private void saveData() {
        userService.saveBatch(cachedDataList);
    }

    @Override
    public String getMsg() {
        return this.msg.toString();
    }
}
