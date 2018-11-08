package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//保存tb_goods表中的数据
		TbGoods tbGoods = goods.getGoods();
		//新录入的商品，都是未审核状态
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);

		//保存tb_goods_desc表中数据
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(goodsDesc);
		if("1".equals(tbGoods.getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				String title = tbGoods.getGoodsName();
				String spec = item.getSpec();
				Map<String,String> specMap = JSON.parseObject(spec, Map.class);
				for(String key:specMap.keySet()){
					title+=""+specMap.get(key);
				}
				item.setTitle(title);
				setItemValue(tbGoods, goodsDesc, item);
				itemMapper.insert(item);
		    }

		}else{
			TbItem item = new TbItem();
			String title = tbGoods.getGoodsName();
			item.setTitle(title);
			setItemValue(tbGoods, goodsDesc, item);
			item.setSpec("{}");
			item.setPrice(tbGoods.getPrice());
			item.setNum(9999);
			item.setStatus("1");
			item.setIsDefault("1");
			itemMapper.insert(item);
		}
	}

	private void setItemValue(TbGoods tbGoods, TbGoodsDesc goodsDesc, TbItem item) {
		String itemImages = goodsDesc.getItemImages();
		List<Map> imageList = JSON.parseArray(itemImages, Map.class);
		if(imageList.size()>0){
            String image=(String)imageList.get(0).get("url");
            item.setImage(image);
        }
		item.setCategoryid(tbGoods.getCategory3Id());
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		item.setGoodsId(tbGoods.getId());
		item.setSellerId(tbGoods.getSellerId());
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		item.setCategory(itemCat.getName());
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		item.setBrand(tbBrand.getName());
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		item.setSellerId(tbSeller.getNickName());
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteIsNull();

		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids,String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public void updateIsMarketable(Long[] ids,String isMarketable) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			if("1".equals(tbGoods.getAuditStatus())){
				tbGoods.setIsMarketable(isMarketable);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}else{
				throw new RuntimeException("只有审核通过的商品才能上下架");
			}

		}
	}


}
