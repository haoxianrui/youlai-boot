package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.converter.DictDataConverter;
import com.youlai.boot.system.mapper.DictItemMapper;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.vo.DictItemPageVO;
import com.youlai.boot.system.service.DictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 字典数据实现类
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    private final DictDataConverter dictDataConverter;

    /**
     * 获取字典数据分页列表
     *
     * @param queryParams
     * @return
     */
    @Override
    public Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams) {
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<DictItemPageVO> page = new Page<>(pageNum, pageSize);

        return this.baseMapper.getDictItemPage(page, queryParams);
    }

    /**
     * 获取字典项表单
     *
     * @param dictCode 字典编码
     * @param itemId 字典数据ID
     * @return
     */
    @Override
    public DictItemForm getDictItemForm(String dictCode,Long itemId) {
        DictItem entity = this.getById(itemId);
        return dictDataConverter.toForm(entity);
    }

    /**
     * 保存字典数据
     *
     * @param formData
     * @return
     */
    @Override
    public boolean saveDictItem(DictItemForm formData) {
        DictItem entity = dictDataConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新字典数据
     *
     * @param formData 字典数据表单
     * @return
     */
    @Override
    public boolean updateDictItem(DictItemForm formData) {
        DictItem entity = dictDataConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID集合
     */
    @Override
    public void deleteDictItemByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
        this.removeByIds(idList);
    }

    /**
     * 获取字典数据列表
     *
     * @param dictCode 字典编码
     * @return
     */
    @Override
    public List<Option<String>> getDictDataList(String dictCode) {
        return this.list(new LambdaQueryWrapper<DictItem>()
                        .eq(DictItem::getDictCode, dictCode)
                        .eq(DictItem::getStatus, 1)
                ).stream().map(item -> new Option<>(item.getValue(), item.getLabel(),item.getTagType()))
                .toList();
    }
}




