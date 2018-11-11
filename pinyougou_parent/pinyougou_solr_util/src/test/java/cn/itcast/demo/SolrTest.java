package cn.itcast.demo;


import com.pinyougou.pojo.TbItem;
import com.pinyougou.solr.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class SolrTest {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private SolrUtil solrUtil;
    @Test
    public void testDataImport(){
        solrUtil.dataImport();
    }

    //新增和修改
    @Test
    public void save(){
        TbItem item = new TbItem();
        item.setId(2L);
        item.setBrand("小米3");
        item.setTitle("小米MIX3 故宫版 全网通");
        item.setSeller("小米旗舰店");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }
    //基于id查询
    @Test
    public void queryById(){
        TbItem item = solrTemplate.getById(1, TbItem.class);
        System.out.println(item.getId() + " " + item.getBrand() + " " + item.getTitle() + " " + item.getSeller());
    }
    //基于ID删除
    @Test
    public void deleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    @Test
    public void deleteAll(){
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
    //批量添加100条
    @Test
    public void saveBatch(){
        List<TbItem> items = new ArrayList<>();
        for(long i=0;i<=100;i++){
            TbItem item = new TbItem();
            item.setId(i);
            item.setBrand("小米");
            item.setTitle(i+"小米MIX3 故宫版 全网通");
            item.setSeller("小米"+i+"旗舰店");
            items.add(item);
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }
    //分页查询
    @Test
    public void queryPage(){
        Query query = new SimpleQuery("*:*");
        query.setOffset(2);//默认起始值：0
        query.setRows(5);
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        for (TbItem item : items) {
            System.out.println(item.getId() + " " + item.getBrand() + " " + item.getTitle() + " " + item.getSeller());
        }
    }
    //条件查询
    @Test
    public void multiQuery(){
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("9").and("item_seller").contains("5");
        query.addCriteria(criteria);
//        query.setOffset(2);//默认起始值：0
//        query.setRows(5);
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        for (TbItem item : items) {
            System.out.println(item.getId() + " " + item.getBrand() + " " + item.getTitle() + " " + item.getSeller());
        }
    }
}
