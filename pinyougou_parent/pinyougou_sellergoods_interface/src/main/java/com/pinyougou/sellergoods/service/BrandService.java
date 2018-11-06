package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询品牌数据
     */
    public PageResult findPage(Integer pageNum,Integer pageSize);

    void add(TbBrand brand);

    TbBrand findOne(Long id);

    void update(TbBrand brand);

    void delete(Long[] ids);

    PageResult findPage(TbBrand brand, int page, int rows);

    List<Map> selectBrandList();
}
