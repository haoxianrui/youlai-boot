package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.vo.DictItemPageVO;

import java.util.List;

/**
 * 字典项接口
 *
 * @author Ray Hao
 * @since 2023/3/4
 */
public interface DictItemService extends IService<DictItem> {

    /**
     * 字典数据分页列表
     *
     * @param queryParams
     * @return
     */
    Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams);

    /**
     * 获取字典数据表单
     *
     * @param dictCode 字典编码
     * @param itemId 字典数据ID
     * @return
     */
    DictItemForm getDictItemForm(String dictCode,Long itemId);

    /**
     * 保存字典项
     *
     * @param formData
     * @return
     */
    boolean saveDictItem(DictItemForm formData);

    /**
     * 更新字典数据
     *
     * @param formData 字典数据表单
     * @return
     */
    boolean updateDictItem(DictItemForm formData);

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID,多个逗号分隔
     */
    void deleteDictItemByIds(String ids);

    /**
     * 获取字典数据列表
     *
     * @param dictCode 字典编码
     * @return
     */
    List<Option<String>> getDictDataList(String dictCode);
}
