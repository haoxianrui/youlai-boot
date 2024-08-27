package com.youlai.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.NoticeStatusMapper;
import com.youlai.system.service.NoticeStatusService;
import com.youlai.system.model.entity.NoticeStatus;
import com.youlai.system.model.form.NoticeStatusForm;
import com.youlai.system.model.query.NoticeStatusQuery;
import com.youlai.system.model.vo.NoticeStatusVO;
import com.youlai.system.converter.NoticeStatusConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 用户公告状态服务实现类
 *
 * @author youlaitech
 * @since 2024-08-27 09:53
 */
@Service
@RequiredArgsConstructor
public class NoticeStatusServiceImpl extends ServiceImpl<NoticeStatusMapper, NoticeStatus> implements NoticeStatusService {

    private final NoticeStatusConverter noticeStatusConverter;

    /**
    * 获取用户公告状态分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<NoticeStatusVO>} 用户公告状态分页列表
    */
    @Override
    public IPage<NoticeStatusVO> getNoticeStatusPage(NoticeStatusQuery queryParams) {
        Page<NoticeStatusVO> pageVO = this.baseMapper.getNoticeStatusPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取用户公告状态表单数据
     *
     * @param id 用户公告状态ID
     * @return
     */
    @Override
    public NoticeStatusForm getNoticeStatusFormData(Long id) {
        NoticeStatus entity = this.getById(id);
        return noticeStatusConverter.toForm(entity);
    }
    
    /**
     * 新增用户公告状态
     *
     * @param formData 用户公告状态表单对象
     * @return
     */
    @Override
    public boolean saveNoticeStatus(NoticeStatusForm formData) {
        NoticeStatus entity = noticeStatusConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新用户公告状态
     *
     * @param id   用户公告状态ID
     * @param formData 用户公告状态表单对象
     * @return
     */
    @Override
    public boolean updateNoticeStatus(Long id,NoticeStatusForm formData) {
        NoticeStatus entity = noticeStatusConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除用户公告状态
     *
     * @param ids 用户公告状态ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteNoticeStatuss(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的用户公告状态数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
