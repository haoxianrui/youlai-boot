package com.youlai.system.service;

import com.youlai.system.model.entity.Notice;
import com.youlai.system.model.form.NoticeForm;
import com.youlai.system.model.query.NoticeQuery;
import com.youlai.system.model.vo.NoticeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 通知公告服务类
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
public interface NoticeService extends IService<Notice> {

    /**
     *通知公告分页列表
     *
     * @return
     */
    IPage<NoticeVO> getNoticePage(NoticeQuery queryParams);

    /**
     * 获取通知公告表单数据
     *
     * @param id 通知公告ID
     * @return
     */
     NoticeForm getNoticeFormData(Long id);

    /**
     * 新增通知公告
     *
     * @param formData 通知公告表单对象
     * @return
     */
    boolean saveNotice(NoticeForm formData);

    /**
     * 修改通知公告
     *
     * @param id   通知公告ID
     * @param formData 通知公告表单对象
     * @return
     */
    boolean updateNotice(Long id, NoticeForm formData);

    /**
     * 删除通知公告
     *
     * @param ids 通知公告ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteNotices(String ids);

}
