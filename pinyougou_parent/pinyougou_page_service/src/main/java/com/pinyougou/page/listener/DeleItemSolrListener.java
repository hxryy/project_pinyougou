package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

public class DeleItemSolrListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage=(TextMessage)message;
            String goodsId = textMessage.getText();
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                new File("D:/item/"+item.getId()+".html").delete();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
