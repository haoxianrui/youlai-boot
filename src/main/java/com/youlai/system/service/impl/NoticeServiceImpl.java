package com.youlai.system.service.impl;

import com.youlai.system.model.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.mapper.NoticeMapper;
import com.youlai.system.service.NoticeService;
import com.youlai.system.model.entity.Notice;
import com.youlai.system.model.form.NoticeForm;
import com.youlai.system.model.query.NoticeQuery;
import com.youlai.system.converter.NoticeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 通知公告服务实现类
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeConverter noticeConverter;

    /**
    * 获取通知公告分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<NoticeVO>} 通知公告分页列表
    */
    @Override
    public IPage<NoticeVO> getNoticePage(NoticeQuery queryParams) {
        Page<NoticeVO> pageVO = this.baseMapper.getNoticePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取通知公告表单数据
     *
     * @param id 通知公告ID
     * @return
     */
    @Override
    public NoticeForm getNoticeFormData(Long id) {
        Notice entity = this.getById(id);
        return noticeConverter.toForm(entity);
    }
    
    /**
     * 新增通知公告
     *
     * @param formData 通知公告表单对象
     * @return
     */
    @Override
    public boolean saveNotice(NoticeForm formData) {
        Notice entity = noticeConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新通知公告
     *
     * @param id   通知公告ID
     * @param formData 通知公告表单对象
     * @return
     */
    @Override
    public boolean updateNotice(Long id,NoticeForm formData) {
        Notice entity = noticeConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除通知公告
     *
     * @param ids 通知公告ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteNotices(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的通知公告数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
