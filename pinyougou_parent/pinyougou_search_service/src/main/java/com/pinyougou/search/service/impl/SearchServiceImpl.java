package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //1.关键字搜索
        String keywords = (String) searchMap.get("keywords");
        Criteria criteria=null;
        if(keywords!=null && !"".equals(keywords)){
            //输入关键字
            criteria=new Criteria("item_keywords").is(keywords);
        }else {
            //未输入关键字
            criteria=new Criteria().expression("*:*");
        }
        //2.基于品牌进行条件过滤查询
        String brand = (String) searchMap.get("brand");
        if(brand!=null && !"".equals(brand)){
            //设置品牌查询条件
            Criteria brandCriteria= new Criteria("item_brand").is(brand);
            //设置过滤查询对象
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }
        //3.基于分类进行条件过滤查询
        String category = (String) searchMap.get("category");
        if(category!=null && !"".equals(category)){
            //设置分类查询条件
            Criteria categoryCriteria= new Criteria("item_category").is(category);
            //设置过滤查询对象
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.基于规格进行条件过滤查询
        Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
        if(specMap!=null){
            for (String key : specMap.keySet()) {
                //设置分类查询条件
                Criteria specCriteria= new Criteria("item_spec_"+key).is(specMap.get(key));
                //设置过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //5.基于价格区间进行条件过滤查询
        String price = (String) searchMap.get("price");
        if(price!=null && !"".equals(price)){
            // 0-500  500-1000  1000-*    考虑价格临界值
            String[] prices = price.split("-");
            if(!"0".equals(prices[0])){
                //设置过滤查询条件
                Criteria priceCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                //设置过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }

            if(!"*".equals(prices[1])){
                //设置过滤查询条件
                Criteria priceCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                //设置过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6 商品排序操作
        String sortField = (String) searchMap.get("sortField");
        String sort = (String) searchMap.get("sort");
        if(sortField!=null && !"".equals(sortField)){
            //设置排序条件
            if("ASC".equals(sort)){
                //升序
                query.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
            }else{
                //降序
                query.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
            }
        }
        //7分页条件查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        query.setOffset((pageNo-1)*pageSize);//分页起始值
        query.setRows(pageSize);//每页记录数据

        //将查询条件付给总查询对象
        query.addCriteria(criteria);
        //设置高亮显示
        HighlightOptions highlightOption = new HighlightOptions();
        //设置高亮字段
        highlightOption.addField("item_title");
        //设置高亮前缀和后缀
        highlightOption.setSimplePrefix("<font color='red'>");
        highlightOption.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOption);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        System.out.println("total count"+page.getTotalElements());
        System.out.println("total page"+page.getTotalPages());
        //满足条件的商品列表数据
        List<TbItem> content = page.getContent();
        //高亮显示结果处理
        for (TbItem item : content) {
            //获取高亮结果值
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);
            if(highlights.size()>0){
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮结果
                List<String> snipplets = highlight.getSnipplets();
                if(snipplets.size()>0){
                    item.setTitle(snipplets.get(0));
                }
            }
        }
        //返回给前端，封装到map集合
        //创建map集合，封装查询结果
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rows",content);
        resultMap.put("pageNo",pageNo);
        resultMap.put("totalPages",page.getTotalPages());
        return resultMap;
    }
}
