package com.youlai.system.service;

import com.youlai.system.model.entity.NoticeStatus;
import com.youlai.system.model.form.NoticeStatusForm;
import com.youlai.system.model.query.NoticeStatusQuery;
import com.youlai.system.model.vo.NoticeStatusVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户公告状态服务类
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
public interface NoticeStatusService extends IService<NoticeStatus> {

    /**
     *用户公告状态分页列表
     *
     * @return
     */
    IPage<NoticeStatusVO> getNoticeStatusPage(NoticeStatusQuery queryParams);

    /**
     * 获取用户公告状态表单数据
     *
     * @param id 用户公告状态ID
     * @return
     */
     NoticeStatusForm getNoticeStatusFormData(Long id);

    /**
     * 新增用户公告状态
     *
     * @param formData 用户公告状态表单对象
     * @return
     */
    boolean saveNoticeStatus(NoticeStatusForm formData);

    /**
     * 修改用户公告状态
     *
     * @param id   用户公告状态ID
     * @param formData 用户公告状态表单对象
     * @return
     */
    boolean updateNoticeStatus(Long id, NoticeStatusForm formData);

    /**
     * 删除用户公告状态
     *
     * @param ids 用户公告状态ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteNoticeStatuss(String ids);

}
