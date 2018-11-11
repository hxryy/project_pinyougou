package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    public void dataImport(){
        List<TbItem> itemList = itemMapper.selectAllGrounding();

        // 处理规格动态域{"机身内存":"16G","网络":"联通3G"}
        for (TbItem item : itemList) {
            String spec = item.getSpec();
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            item.setSpecMap(specMap);
        }
        //将商品导入索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("dataImport finish...");
    }

}
