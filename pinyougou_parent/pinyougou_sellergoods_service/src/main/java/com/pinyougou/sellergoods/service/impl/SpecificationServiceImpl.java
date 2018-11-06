package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, TbSpecification specification) {
        //分页
        PageHelper.startPage(pageNum,pageSize);
        TbSpecificationExample example = new TbSpecificationExample();
        if(specification !=null){
            String specName = specification.getSpecName();
            if(specName!= null && !"".equals(specName)){
                //设置查询条件
                TbSpecificationExample.Criteria criteria = example.createCriteria();
                criteria.andSpecNameLike("%"+specName+"%");
            }
        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public void add(Specification specification) {
        //保存规格数据
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.insert(tbSpecification);

        //保存规格选项
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();

        for (TbSpecificationOption specificationOption : specificationOptions) {
            //设置规格选择关联规格id
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }

    }

    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);

        //查询规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptions(specificationOptions);
        return specification;
    }

    @Override
    public void update(Specification specification) {
        //修改规格表
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //修改规格选项
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();//页面传递的修改后的规格选择值
        //修改规格选择操作  先删除原有的规格选择，再新增页面传递的修改后的规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);

        //新增页面传递的修改后的规格选项
        for (TbSpecificationOption specificationOption : specificationOptions) {
            //设置规格选择关联规格id
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格
            specificationMapper.deleteByPrimaryKey(id);

            //删除规格选项
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public List<Map> selectSpecList() {
        return specificationMapper.selectSpecList();
    }
}
