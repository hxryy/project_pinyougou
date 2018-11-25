package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface PayService {
    public Map<String,Object> createNative(String out_trade_no,String total_fee) throws Exception;
    //查询支付状态的方法
    public Map<String,String> queryPayStatus(String out_trade_no) throws Exception;

    TbPayLog findPayLogByUserId(String userId);

    void updateStatus(String out_trade_no, String transaction_id);
}
