package com.pinyougou.sms.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.sms.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 品优购短信平台，发送短信接口
 * 接口：就是发送短信的请求地址
 */
@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsUtil smsUtil;
    /**
     * 调用工具类发送短信
     * http://localhost:7788/sms/sendSms.do
     */
    @RequestMapping(value = "/sendSms",method = RequestMethod.POST)
    public Map<String,String> sendSms(String phoneNumbers,String signName,String templateCode,String param){
        try {
            SendSmsResponse response = smsUtil.sendSms(phoneNumbers, signName, templateCode, param);
            Map<String, String> resultMap = new HashMap<>();
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
            resultMap.put("Code=",response.getCode());
            resultMap.put("Message=",response.getMessage());
            resultMap.put("RequestId=",response.getRequestId());
            resultMap.put("BizId=",response.getBizId());
            return resultMap;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}
