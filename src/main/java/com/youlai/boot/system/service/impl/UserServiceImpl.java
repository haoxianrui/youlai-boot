package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.common.enums.ContactType;
import com.youlai.boot.platform.mail.service.MailService;
import com.youlai.boot.platform.sms.service.SmsService;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.form.*;
import com.youlai.boot.config.property.AliyunSmsProperties;
import com.youlai.boot.system.converter.UserConverter;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.core.security.util.SecurityUtils;
import com.youlai.boot.system.mapper.UserMapper;
import com.youlai.boot.system.model.dto.UserAuthInfo;
import com.youlai.boot.system.model.bo.UserBO;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.dto.UserExportDTO;
import com.youlai.boot.system.model.vo.UserInfoVO;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.core.security.service.PermissionService;
import com.youlai.boot.system.service.RoleMenuService;
import com.youlai.boot.system.service.RoleService;
import com.youlai.boot.system.service.UserRoleService;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户业务实现类
 *
 * @author haoxr
 * @since 2022/1/14
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRoleService userRoleService;

    private final UserConverter userConverter;

    private final RoleMenuService roleMenuService;

    private final RoleService roleService;

    private final PermissionService permissionService;

    private final SmsService smsService;

    private final MailService mailService;

    private final AliyunSmsProperties aliyunSmsProperties;

    private final StringRedisTemplate redisTemplate;

    /**
     * 获取用户分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<UserPageVO>} 用户分页列表
     */
    @Override
    public IPage<UserPageVO> listPagedUsers(UserPageQuery queryParams) {

        // 参数构建
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<UserBO> page = new Page<>(pageNum, pageSize);
        // 查询数据
        Page<UserBO> userPage = this.baseMapper.listPagedUsers(page, queryParams);

        // 实体转换
        return userConverter.toPageVo(userPage);
    }

    /**
     * 获取用户表单数据
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public UserForm getUserFormData(Long userId) {
        return this.baseMapper.getUserFormData(userId);
    }

    /**
     * 新增用户
     *
     * @param userForm 用户表单对象
     * @return
     */
    @Override
    public boolean saveUser(UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Assert.isTrue(count == 0, "用户名已存在");

        // 实体转换 form->entity
        User entity = userConverter.toEntity(userForm);

        // 设置默认加密密码
        String defaultEncryptPwd = passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD);
        entity.setPassword(defaultEncryptPwd);

        // 新增用户
        boolean result = this.save(entity);

        if (result) {
            // 保存用户角色
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 更新用户
     *
     * @param userId   用户ID
     * @param userForm 用户表单对象
     * @return
     */
    @Override
    @Transactional
    public boolean updateUser(Long userId, UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .ne(User::getId, userId)
        );
        Assert.isTrue(count == 0, "用户名已存在");

        // form -> entity
        User entity = userConverter.toEntity(userForm);

        // 修改用户
        boolean result = this.updateById(entity);

        if (result) {
            // 保存用户角色
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 删除用户
     *
     * @param idsStr 用户ID，多个以英文逗号(,)分割
     * @return true|false
     */
    @Override
    public boolean deleteUsers(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除的用户数据为空");
        // 逻辑删除
        List<Long> ids = Arrays.stream(idsStr.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return this.removeByIds(ids);

    }

    /**
     * 根据用户名获取认证信息
     *
     * @param username 用户名
     * @return 用户认证信息 {@link UserAuthInfo}
     */
    @Override
    public UserAuthInfo getUserAuthInfo(String username) {
        UserAuthInfo userAuthInfo = this.baseMapper.getUserAuthInfo(username);
        if (userAuthInfo != null) {
            Set<String> roles = userAuthInfo.getRoles();
            if (CollectionUtil.isNotEmpty(roles)) {
                Set<String> perms = roleMenuService.getRolePermsByRoleCodes(roles);
                userAuthInfo.setPerms(perms);
            }

            // 获取最大范围的数据权限
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthInfo.setDataScope(dataScope);
        }
        return userAuthInfo;
    }


    /**
     * 获取导出用户列表
     *
     * @param queryParams 查询参数
     * @return {@link List< UserExportDTO >} 导出用户列表
     */
    @Override
    public List<UserExportDTO> listExportUsers(UserPageQuery queryParams) {
        return this.baseMapper.listExportUsers(queryParams);
    }

    /**
     * 获取登录用户信息
     *
     * @return {@link UserInfoVO}   用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {

        String username = SecurityUtils.getUsername();

        // 获取登录用户基础信息
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .select(
                        User::getId,
                        User::getUsername,
                        User::getNickname,
                        User::getAvatar
                )
        );
        // entity->VO
        UserInfoVO userInfoVO = userConverter.toUserInfoVo(user);

        // 用户角色集合
        Set<String> roles = SecurityUtils.getRoles();
        userInfoVO.setRoles(roles);

        // 用户权限集合
        if (CollectionUtil.isNotEmpty(roles)) {
            Set<String> perms = permissionService.getRolePermsFormCache(roles);
            userInfoVO.setPerms(perms);
        }
        return userInfoVO;
    }

    /**
     * 获取个人中心用户信息
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserBO entity = this.baseMapper.getUserProfile(userId);
        return userConverter.toProfileVO(entity);
    }

    /**
     * 修改个人中心用户信息
     *
     * @param formData 表单数据
     * @return
     */
    @Override
    public boolean updateUserProfile(UserProfileForm formData) {
        Long userId = SecurityUtils.getUserId();
        User entity = userConverter.toEntity(formData);
        entity.setId(userId);
        return this.updateById(entity);
    }


    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param data   密码修改表单数据
     * @return
     */
    @Override
    public boolean changePassword(Long userId, PasswordChangeForm data) {

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String oldPassword = data.getOldPassword();

        // 校验原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        // 新旧密码不能相同
        if (passwordEncoder.matches(data.getNewPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        String newPassword = data.getNewPassword();
        return this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(newPassword))
        );
    }

    /**
     * 重置密码
     *
     * @param userId   用户ID
     * @param password 密码重置表单数据
     * @return
     */
    @Override
    public boolean resetPassword(Long userId, String password) {
        return this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(password))
        );
    }

    /**
     * 发送验证码
     *
     * @param contact 联系方式 手机号/邮箱
     * @param type    联系方式类型 {@link ContactType}
     * @return
     */
    @Override
    public boolean sendVerificationCode(String contact, ContactType type) {

        // 随机生成4位验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // 发送验证码

        String verificationCodePrefix = null;
        switch (type) {
            case MOBILE:
                // 获取修改密码的模板code
                String changePasswordSmsTemplateCode = aliyunSmsProperties.getTemplateCodes().get("changePassword");
                smsService.sendSms(contact, changePasswordSmsTemplateCode, "[{\"code\":\"" + code + "\"}]");
                verificationCodePrefix = RedisConstants.MOBILE_VERIFICATION_CODE_PREFIX;
                break;
            case EMAIL:
                mailService.sendMail(contact, "验证码", "您的验证码是：" + code);
                verificationCodePrefix = RedisConstants.EMAIL_VERIFICATION_CODE_PREFIX;
                break;
            default:
                throw new BusinessException("不支持的联系方式类型");
        }
        // 存入 redis 用于校验, 5分钟有效
        redisTemplate.opsForValue().set(verificationCodePrefix + contact, code, 5, TimeUnit.MINUTES );
        return true;
    }

    /**
     * 修改当前用户手机号码
     *
     * @param data 表单数据
     * @return
     */
    @Override
    public boolean bindMobile(MobileBindingForm data) {
        Long userId = SecurityUtils.getUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 校验验证码
        String verificationCode = data.getCode();
        String contact = data.getMobile();
        String verificationCodeKey = RedisConstants.MOBILE_VERIFICATION_CODE_PREFIX + contact;
        String code = redisTemplate.opsForValue().get(verificationCodeKey);
        if (!verificationCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        // 更新手机号码
        return this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getMobile, contact)
        );
    }

    /**
     * 修改当前用户邮箱
     *
     * @param data 表单数据
     * @return
     */
    @Override
    public boolean bindEmail(EmailChangeForm data) {
        Long userId = SecurityUtils.getUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 校验验证码
        String verificationCode = data.getCode();
        String email = data.getEmail();
        String verificationCodeKey = RedisConstants.EMAIL_VERIFICATION_CODE_PREFIX + email;
        String code = redisTemplate.opsForValue().get(verificationCodeKey);
        if (!verificationCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        // 更新邮箱
        return this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getEmail, email)
        );
    }
}
