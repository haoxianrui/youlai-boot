package com.youlai.boot.system.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.exception.BusinessException;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.converter.DictConverter;
import com.youlai.boot.system.mapper.DictMapper;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.system.service.DictItemService;
import com.youlai.boot.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典业务实现类
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    private final DictItemService dictItemService;
    private final DictConverter dictConverter;

    /**
     * 字典分页列表
     *
     * @param queryParams 分页查询对象
     */
    @Override
    public Page<DictPageVO> getDictPage(DictPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();

        // 查询数据
        return this.baseMapper.getDictPage(new Page<>(pageNum, pageSize), queryParams);
    }

    /**
     * 获取字典列表
     *
     * @return 字典列表
     */
    @Override
    public List<Option<String>> getDictList() {
        return this.list(new LambdaQueryWrapper<Dict>().eq(Dict::getStatus, 1))
                .stream().map(item ->
                        new Option<>(item.getDictCode(), item.getName()))
                .toList();
    }


    /**
     * 新增字典
     *
     * @param dictForm 字典表单数据
     */
    @Override
    public boolean saveDict(DictForm dictForm) {
        // 保存字典
        Dict entity = dictConverter.toEntity(dictForm);

        // 校验 code 是否唯一
        String dictCode = entity.getDictCode();

        long count = this.count(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getDictCode, dictCode)
        );

        Assert.isTrue(count == 0, "字典编码已存在");

        return this.save(entity);
    }


    /**
     * 获取字典表单详情
     *
     * @param id 字典ID
     */
    @Override
    public DictForm getDictForm(Long id) {
        // 获取字典
        Dict entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("字典不存在");
        }
        return dictConverter.toForm(entity);
    }

    /**
     * 修改字典
     *
     * @param id       字典ID
     * @param dictForm 字典表单
     */
    @Override
    public boolean updateDict(Long id, DictForm dictForm) {
        // 更新字典
        Dict entity = dictConverter.toEntity(dictForm);

        // 校验 code 是否唯一
        String dictCode = entity.getDictCode();
        long count = this.count(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getDictCode, dictCode)
                .ne(Dict::getId, id)
        );
        if (count > 0) {
            throw new BusinessException("字典编码已存在");
        }

        return this.updateById(entity);
    }

    /**
     * 删除字典
     *
     * @param ids 字典ID，多个以英文逗号(,)分割
     */
    @Override
    @Transactional
    public void deleteDictByIds(List<String> ids) {
        for (String id : ids) {
            Dict dict = this.getById(id);
            if (dict != null) {
                boolean removeResult = this.removeById(id);
                // 删除字典下的字典项
                if (removeResult) {
                    dictItemService.remove(
                            new LambdaQueryWrapper<DictItem>()
                                    .eq(DictItem::getDictCode, dict.getDictCode())
                    );
                }

            }
        }
    }


    /**
     * 获取字典项列表
     *
     * @param dictCode 字典编码
     */
    @Override
    public List<DictItemOptionVO> getDictItems(String dictCode) {
        return dictItemService.list(
                        new LambdaQueryWrapper<DictItem>()
                                .eq(DictItem::getDictCode, dictCode)
                                .eq(DictItem::getStatus, 1)
                                .orderByAsc(DictItem::getSort)
                ).stream()
                .map(item -> {
                    DictItemOptionVO dictItemOptionVO = new DictItemOptionVO();
                    dictItemOptionVO.setLabel(item.getLabel());
                    dictItemOptionVO.setValue(item.getValue());
                    dictItemOptionVO.setTagType(item.getTagType());
                    return dictItemOptionVO;
                }).toList();
    }
}




