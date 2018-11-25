package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class PayServiceImpl implements PayService {
    @Value("${appid}")
    private String appid;//公众号id
    @Value("${partner}")
    private String partner;//商户号id
    @Value("${partnerkey}")
    private String partnerkey;//商户秘钥
    @Value("${notifyurl}")
    private String notifyurl;//回调地址
    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) throws Exception {
        //封装微信支付所需要的参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body","品优购");
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("total_fee",total_fee);
        paramMap.put("spbill_create_ip","127.0.0.1");
        paramMap.put("notify_url",notifyurl);
        paramMap.put("trade_type","NATIVE");
        paramMap.put("product_id","1");
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println(paramXml);
        //调用微信支付统一下单接口，完成获取支付链接操作
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();
        //处理响应结果
        String resultXml = httpClient.getContent();
        System.out.println(resultXml);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
        String code_url = resultMap.get("code_url");
        Map<String,Object> map = new HashMap<>();
        map.put("code_url",code_url);
        map.put("out_trade_no",out_trade_no);
        map.put("total_fee",total_fee);
        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) throws Exception {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println(paramXml);
        //调用微信支付查询接口，完成查询操作
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();

        //处理响应结果
        String resultXml = httpClient.getContent();
        System.out.println(resultXml);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
        return resultMap;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public TbPayLog findPayLogByUserId(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Autowired
    private TbPayLogMapper payLogMapper;
    @Autowired
    private TbOrderMapper orderMapper;
    @Override
    public void updateStatus(String out_trade_no, String transaction_id) {
        //更新支付日志
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("2");
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKey(payLog);
        //更新订单
        String orderList = payLog.getOrderList();
        String[] ids = orderList.split(",");
        for (String id : ids) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(id));
            tbOrder.setPaymentTime(new Date());
            tbOrder.setStatus("2");
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //清除缓存中支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());


    }
}
