package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.converter.DictConverter;
import com.youlai.system.converter.DictItemConverter;
import com.youlai.system.model.entity.SysDict;
import com.youlai.system.model.entity.SysDictItem;
import com.youlai.system.common.model.Option;
import com.youlai.system.mapper.SysDictMapper;
import com.youlai.system.model.form.DictForm;
import com.youlai.system.model.query.DictTypePageQuery;
import com.youlai.system.model.vo.DictPageVO;
import com.youlai.system.service.SysDictItemService;
import com.youlai.system.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典业务实现类
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {


    private final SysDictItemService dictItemService;
    private final DictConverter dictConverter;
    private final DictItemConverter dictItemConverter;

    /**
     * 字典分页列表
     *
     * @param queryParams 分页查询对象
     * @return
     */
    @Override
    public Page<DictPageVO> getDictPage(DictTypePageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();

        // 查询数据
        return this.baseMapper.getDictPage(new Page<>(pageNum, pageSize), queryParams);
    }


    /**
     * 新增字典
     *
     * @param dictForm 字典表单数据
     * @return
     */
    @Override
    public boolean saveDict(DictForm dictForm) {
        // 保存字典
        SysDict entity = dictConverter.convertToEntity(dictForm);
        boolean result = this.save(entity);
        // 保存字典项
        if (result) {
            List<SysDictItem> dictItems = dictItemConverter.convertToEntity(dictForm.getDictItems());
            dictItems.forEach(dictItem -> dictItem.setDictId(entity.getId()));
            dictItemService.saveBatch(dictItems);
        }
        return result;
    }


    /**
     * 获取字典表单详情
     *
     * @param id 字典ID
     * @return
     */
    @Override
    public DictForm getDictForm(Long id) {
        // 获取字典
        SysDict entity = this.getById(id);
        Assert.isTrue(entity != null, "字典不存在");
        DictForm dictForm = dictConverter.convertToForm(entity);

        // 获取字典项集合
        List<SysDictItem> dictItems = dictItemService.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, id)
        );
        // 转换数据项
        List<DictForm.DictItem> dictItemList = dictItemConverter.convertToDictForm(dictItems);
        dictForm.setDictItems(dictItemList);
        return dictForm;
    }


    /**
     * 修改字典
     *
     * @param id       字典ID
     * @param dictForm 字典表单
     * @return
     */
    @Override
    public boolean updateDict(Long id, DictForm dictForm) {
        // 更新字典
        SysDict entity = dictConverter.convertToEntity(dictForm);
        boolean result = this.updateById(entity);

        if (result) {
            // 更新字典项
            List<AttrGroupForm.Attr> attrList = formData.getAttrs();
            List<Attr> attrEntities = attrConverter.convertToEntity(attrList);

            // 获取当前组的所有属性
            List<Attr> currentAttrEntities = attrService.list(new LambdaQueryWrapper<Attr>()
                    .eq(Attr::getAttrGroupId, groupId)
            );

            // 获取当前数据库中存在的属性ID集合
            Set<Long> currentAttrIds = currentAttrEntities.stream()
                    .map(Attr::getId)
                    .collect(Collectors.toSet());

            // 获取新提交的属性ID集合
            Set<Long> newAttrIds = attrEntities.stream()
                    .map(Attr::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 需要删除的属性ID集合（存在于数据库但不在新提交的属性中）
            Set<Long> idsToDelete = new HashSet<>(currentAttrIds);
            idsToDelete.removeAll(newAttrIds);

            // 删除不在新提交属性中的旧属性
            if (!idsToDelete.isEmpty()) {
                attrService.removeByIds(idsToDelete);
            }

            // 更新或新增属性
            for (Attr attr : attrEntities) {
                if (attr.getId() != null && currentAttrIds.contains(attr.getId())) {
                    // 更新现有属性
                    attrService.updateById(attr);
                } else {
                    // 新增属性
                    attr.setAttrGroupId(groupId);
                    attrService.save(attr);
                }
            }
        }
        return result;
    }

    /**
     * 删除字典
     *
     * @param ids 字典ID，多个以英文逗号(,)分割
     * @return
     */
    @Override
    @Transactional
    public boolean deleteDictByIds(String ids) {

        Assert.isTrue(StrUtil.isNotBlank(ids), "请选择需要删除的字典");

        List<String> idList = Arrays.stream(ids.split(","))
                .toList();

        // 删除字典数据项
        List<String> dictTypeCodes = this.list(new LambdaQueryWrapper<SysDict>()
                        .in(SysDict::getId, ids)
                        .select(SysDict::getCode))
                .stream()
                .map(dictType -> dictType.getCode())
                .collect(Collectors.toList()
                );
        if (CollectionUtil.isNotEmpty(dictTypeCodes)) {
            dictItemService.remove(new LambdaQueryWrapper<SysDictItem>()
                    .in(SysDictItem::getTypeCode, dictTypeCodes));
        }
        // 删除字典
        boolean result = this.removeByIds(idList);
        return result;
    }

    /**
     * 获取字典的数据项
     *
     * @param typeCode
     * @return
     */
    @Override
    public List<Option> listDictItemsByTypeCode(String typeCode) {
        // 数据字典项
        List<SysDictItem> dictItems = dictItemService.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getTypeCode, typeCode)
                .select(SysDictItem::getValue, SysDictItem::getName)
        );

        // 转换下拉数据
        List<Option> options = CollectionUtil.emptyIfNull(dictItems)
                .stream()
                .map(dictItem -> new Option(dictItem.getValue(), dictItem.getName()))
                .collect(Collectors.toList());
        return options;
    }


}




