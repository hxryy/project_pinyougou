package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private PayService payService;
    /**
     * 生成二维码
     */
    @RequestMapping("/createNative")
    public Map<String,Object> createNative(){
        // 随机生成订单号
        IdWorker idWorker = new IdWorker();
        try {
            //基于用户名获取支付日志
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbPayLog payLog = payService.findPayLogByUserId(userId);
            return payService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 查询支付状态
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        int i=1;
        try {
            //持续性查询用户支付状态
            while(true){
                //每隔3秒查询一次
                Thread.sleep(3000);
                //如果用户5分钟没有支付，支付超时跳出循环
                i++;
                if(i>=100){
                    return new Result(false,"timeout");
                }
                Map<String, String> resultMap = payService.queryPayStatus(out_trade_no);
                //获取支付状态
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    //支付成功,获取微信返回的交易流水号
                    String transaction_id = resultMap.get("transaction_id");
                    //支付成功后，更新订单状态和支付日志状态
                    payService.updateStatus(out_trade_no,transaction_id);
                    return new Result(true,"支付成功");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }

    }
}
