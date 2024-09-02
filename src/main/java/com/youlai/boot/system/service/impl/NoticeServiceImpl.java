package com.youlai.boot.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.constant.SymbolConstant;
import com.youlai.boot.common.enums.MessageTypeEnum;
import com.youlai.boot.common.util.CommonUtil;
import com.youlai.boot.core.security.util.SecurityUtils;
import com.youlai.boot.system.converter.NoticeConverter;
import com.youlai.boot.system.handler.MessageHandler;
import com.youlai.boot.system.mapper.NoticeMapper;
import com.youlai.boot.system.model.bo.NoticeBO;
import com.youlai.boot.system.model.dto.MessageDTO;
import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.entity.NoticeStatus;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.query.NoticeQuery;
import com.youlai.boot.system.model.vo.NoticeVO;
import com.youlai.boot.system.service.NoticeService;
import com.youlai.boot.system.service.NoticeStatusService;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final MessageHandler messageHandler;

    private final NoticeStatusService noticeStatusService;

    private final UserService userService;

    /**
     * 获取通知公告分页列表
     *
     * @param queryParams 查询参数
     * @return  {@link IPage<NoticeVO>} 通知公告分页列表
     */
    @Override
    public IPage<NoticeVO> getNoticePage(NoticeQuery queryParams) {
        Page<NoticeBO> noticePage = this.baseMapper.getNoticePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return noticeConverter.toPageVo(noticePage);
    }

    /**
     * 获取通知公告表单数据
     *
     * @param id 通知公告ID
     * @return {@link NoticeForm} 通知公告表单对象
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
     * @return {@link Boolean} 是否新增成功
     */
    @Override
    public boolean saveNotice(NoticeForm formData) {
        Notice entity = noticeConverter.toEntity(formData);
        entity.setReleaseStatus(0);
        entity.setCreateBy(SecurityUtils.getUserId());
        if (entity.getTarType() == 1) {
            Assert.notBlank(entity.getTarIds(), "指定用户不能为空");
        }
        return this.save(entity);
    }

    /**
     * 更新通知公告
     *
     * @param id       通知公告ID
     * @param formData 通知公告表单对象
     * @return {@link Boolean} 是否更新成功
     */
    @Override
    public boolean updateNotice(Long id, NoticeForm formData) {
        Notice entity = noticeConverter.toEntity(formData);
        entity.setUpdateBy(SecurityUtils.getUserId());
        if (entity.getTarType() == 1) {
            Assert.notBlank(entity.getTarIds(), "指定用户不能为空");
        }
        return this.updateById(entity);
    }

    /**
     * 删除通知公告
     *
     * @param ids 通知公告ID，多个以英文逗号(,)分割
     * @return {@link Boolean} 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotices(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的通知公告数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(SymbolConstant.COMMA))
                .map(Long::parseLong)
                .toList();
        boolean b = this.removeByIds(idList);
        if (b) {
            //删除通知公告的同时，需要删除通知公告对应的用户通知状态
            noticeStatusService.remove(new LambdaQueryWrapper<NoticeStatus>().in(NoticeStatus::getNoticeId, idList));
        }

        return true;
    }

    /**
     * 发布通知公告
     * @param id 通知公告ID
     * @return 是否发布成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseNotice(Long id) {
        Notice notice = this.getById(id);
        Assert.notNull(notice, "通知公告不存在");
        Assert.isTrue(notice.getReleaseStatus() != 1, "通知公告已发布");
        notice.setReleaseStatus(1);
        notice.setReleaseBy(SecurityUtils.getUserId());
        notice.setReleaseTime(LocalDateTime.now());
        this.updateById(notice);
        //发布通知公告的同时，需要将通知公告发送给目标用户
        //先删除掉该通知公告之前对应的用户信息
        noticeStatusService.remove(new LambdaQueryWrapper<NoticeStatus>().eq(NoticeStatus::getNoticeId, id));
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (notice.getTarType() == 1) {
            Assert.notBlank(notice.getTarIds(), "指定用户不能为空");
            queryWrapper.in(User::getId, Arrays.asList(notice.getTarIds().split(SymbolConstant.COMMA)));
        }
        //查询出目标用户，增加用户通知状态
        List<User> list = userService.list(queryWrapper);
        List<NoticeStatus> needSaveList = list.stream().map(user -> {
            NoticeStatus noticeStatus = new NoticeStatus();
            noticeStatus.setNoticeId(id);
            noticeStatus.setUserId(user.getId());
            noticeStatus.setReadStatus(0);
            return noticeStatus;
        }).toList();
        if(needSaveList.size() > 0){
            noticeStatusService.saveBatch(needSaveList);
        }
        //最后，给当前在线的用户发送websocket消息
        List<String> usernameList = null;
        if(notice.getTarType() == 1){
            List<Long> collect = needSaveList.stream().map(NoticeStatus::getUserId).collect(Collectors.toList());
            List<User> userList = userService.list(new LambdaQueryWrapper<User>().in(User::getId, collect).select(User::getUsername));
            usernameList = userList.stream().map(User::getUsername).collect(Collectors.toList());
        }
        MessageDTO message = new MessageDTO();
        message.setMessageType(MessageTypeEnum.WEBSOCKET);
        message.setReceiver(usernameList);
        message.setContent(getNoticeContent(notice));
        message.setSender(SecurityUtils.getUsername());
        messageHandler.sendMessage(message);
        return this.updateById(notice);
    }

    /**
     * 自定义组合公告内容
     *
     * @param notice 通知公告
     * @return 自定义组合通知公告内容
     */
    private String getNoticeContent(Notice notice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("id", notice.getId());
        jsonObject.set("title", notice.getTitle());
        jsonObject.set("messageType", notice.getNoticeType());
        jsonObject.set("releaseTime", notice.getReleaseTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        jsonObject.set("type", "release");
        return jsonObject.toString();
    }

    /**
     * 撤回通知公告
     *
     * @param id 通知公告ID
     * @return 是否撤回成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recallNotice(Long id) {
        Notice notice = this.getById(id);
        Assert.notNull(notice, "通知公告不存在");
        Assert.isTrue(notice.getReleaseStatus() == 1, "通知公告未发布");
        notice.setReleaseStatus(2);
        notice.setRecallTime(LocalDateTime.now());
        if (!this.updateById(notice)) {
            return false;
        }
        //先删除掉该通知公告之前对应的用户信息
        noticeStatusService.remove(new LambdaQueryWrapper<NoticeStatus>().eq(NoticeStatus::getNoticeId, id));
        return true;
    }

}
