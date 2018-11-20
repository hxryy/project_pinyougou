package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemPageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage=(TextMessage)message;
            String goodsId = textMessage.getText();
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Goods goods = itemPageService.findOne(Long.parseLong(goodsId));
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                Map<String,Object> map = new HashMap<>();
                map.put("item",item);
                map.put("goods",goods);
                Writer out = new FileWriter("D:/item/"+item.getId()+".html");
                template.process(map,out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
