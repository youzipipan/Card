package com.example.card.utils;

import net.sf.json.JSONObject;

public class ResponseUtils {

    public static Object ok(){
        JSONObject json = new JSONObject();
        json.put("state","0");
        json.put("msg","成功");
        json.put("data","");
        return json;
    }

    public static Object ok(String msg){
        JSONObject json = new JSONObject();
        json.put("state","0");
        json.put("msg",msg);
        json.put("data","");
        return json;
    }

    public static Object ok(String msg,Object data){
        JSONObject json = new JSONObject();
        json.put("state","0");
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }

    public static Object fail(int state,String msg){
        JSONObject json = new JSONObject();
        json.put("state",state);
        json.put("msg",msg);
        json.put("data","");
        return json;
    }

    public static Object fail(int state,String msg,Object data){
        JSONObject json = new JSONObject();
        json.put("state",state);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }

    public static Object fail(){
        JSONObject json = new JSONObject();
        json.put("state",1);
        json.put("msg","失败");
        json.put("data","");
        return json;
    }

}
