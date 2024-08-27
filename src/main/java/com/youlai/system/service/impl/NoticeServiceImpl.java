package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.*;
import com.youlai.system.security.util.SecurityUtils;
import com.youlai.system.service.WebsocketService;
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
import com.youlai.system.model.vo.NoticeVO;
import com.youlai.system.converter.NoticeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 通知公告服务实现类
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeConverter noticeConverter;

    private final WebsocketService webSocketServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) ->
                    new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (jsonElement, type, jsonDeserializationContext) ->
                    LocalDateTime.parse(jsonElement.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    private void sendWebSocketMsg(Notice notice) {
        if(notice.getSendStatus() > 0){
            String jsonNotice = gson.toJson(noticeConverter.toVO(notice));
             webSocketServer.sendStringToFrontend(jsonNotice);
        }
    }

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
        entity.setCreateBy(SecurityUtils.getUserId());
        entity.setReleaseBy(SecurityUtils.getUserId());
        entity.setUpdateBy(SecurityUtils.getUserId());
        entity.setIsDelete(0);
        boolean result = this.save(entity);
        if(result){
            sendWebSocketMsg(entity);
        }
        return result;
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
        entity.setUpdateBy(SecurityUtils.getUserId());
        entity.setIsDelete(0);
        boolean result = this.updateById(entity);
        if(result) {
            sendWebSocketMsg(entity);
        }
        return result;
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
        LambdaUpdateWrapper<Notice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Notice::getId, idList).set(Notice::getIsDelete, 1);
        return this.update(wrapper);
    }

}
