package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.converter.DictItemConverter;
import com.youlai.system.mapper.SysDictItemMapper;
import com.youlai.system.model.entity.SysDictItem;
import com.youlai.system.model.form.DictForm;
import com.youlai.system.model.query.DictPageQuery;
import com.youlai.system.model.vo.DictPageVO;
import com.youlai.system.common.model.Option;
import com.youlai.system.service.SysDictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典 服务实现类
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    private final DictItemConverter dictConverter;

    /**
     * 字典数据项分页列表
     *
     * @param queryParams
     * @return
     */
    @Override
    public Page<DictPageVO> getDictPage(DictPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        String typeCode = queryParams.getTypeCode();

        // 查询数据
        Page<SysDictItem> dictItemPage = this.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysDictItem>()
                        .like(StrUtil.isNotBlank(keywords), SysDictItem::getName, keywords)
                        .eq(StrUtil.isNotBlank(typeCode), SysDictItem::getTypeCode, typeCode)
                        .select(SysDictItem::getId, SysDictItem::getName, SysDictItem::getValue, SysDictItem::getStatus)
        );

        // 实体转换
        Page<DictPageVO> pageResult = dictConverter.convertToPageVo(dictItemPage);
        return pageResult;
    }

    /**
     * 字典数据项表单详情
     *
     * @param id 字典数据项ID
     * @return
     */
    @Override
    public DictForm getDictForm(Long id) {
        // 获取entity
        SysDictItem entity = this.getOne(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getId, id)
                .select(
                        SysDictItem::getId,
                        SysDictItem::getTypeCode,
                        SysDictItem::getName,
                        SysDictItem::getValue,
                        SysDictItem::getStatus,
                        SysDictItem::getSort,
                        SysDictItem::getRemark
                ));
        Assert.isTrue(entity != null, "字典数据项不存在");

        // 实体转换
        DictForm dictForm = dictConverter.convertToForm(entity);
        return dictForm;
    }

    /**
     * 新增字典数据项
     *
     * @param dictForm 字典数据项表单
     * @return
     */
    @Override
    public boolean saveDict(DictForm dictForm) {
        // 实体对象转换 form->entity
        SysDictItem entity = dictConverter.convertToEntity(dictForm);
        // 持久化
        boolean result = this.save(entity);
        return result;
    }

    /**
     * 修改字典数据项
     *
     * @param id           字典数据项ID
     * @param dictForm 字典数据项表单
     * @return
     */
    @Override
    public boolean updateDict(Long id, DictForm dictForm) {
        SysDictItem entity = dictConverter.convertToEntity(dictForm);
        boolean result = this.updateById(entity);
        return result;
    }

    /**
     * 删除字典数据项
     *
     * @param idsStr 字典数据项ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    public boolean deleteDict(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "删除数据为空");
        //
        List<Long> ids = Arrays.asList(idsStr.split(","))
                .stream()
                .map(id -> Long.parseLong(id))
                .collect(Collectors.toList());

        // 删除字典数据项
        boolean result = this.removeByIds(ids);
        return result;
    }

    /**
     * 获取字典下拉列表
     *
     * @param typeCode
     * @return
     */
    @Override
    public List<Option> listDictOptions(String typeCode) {
        // 数据字典项
        List<SysDictItem> dictList = this.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getTypeCode, typeCode)
                .select(SysDictItem::getValue, SysDictItem::getName)
        );

        // 转换下拉数据
        List<Option> options = CollectionUtil.emptyIfNull(dictList)
                .stream()
                .map(dictItem -> new Option(dictItem.getValue(), dictItem.getName()))
                .collect(Collectors.toList());
        return options;
    }
}




