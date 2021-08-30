package com.controller;

import com.server.OrderServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    private OrderServer orderServer;


    @PostMapping("/purchase")
    public Object purchase(){
        Map map = new HashMap();
        map.put("code",200);
        try{
            if(orderServer.createOrder()){
                map.put("inform","下单成功");
            }else{
                map.put("inform","抢完了");
            }
        }catch (Exception e){
            map.put("statu","failed in querying");
        }
        return map;
    }
}
