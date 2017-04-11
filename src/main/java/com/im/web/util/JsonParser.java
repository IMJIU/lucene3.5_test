//package com.im.web.util;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//
//import org.apache.commons.lang3.StringUtils;
//import org.jdom.IllegalAddException;
//
///**
// * 专门处理json格式的解析
// *
// * @author 陈谋坤 (chenmoukun@pdpower.com)
// * @version Revision: 1.00 Date：Apr 14, 2008
// */
//public class JsonParser
//{
//    /**
//     * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the hierarchy的方法
//     * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
//     *
//     * @param obj
//     * @return
//     */
//    public static JSONObject getJsonObject(Object obj){
//
//        JsonConfig jsonConfig = new JsonConfig();
//        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//        return JSONObject.fromObject(obj, jsonConfig);
//    }
//
//    /**
//     * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the hierarchy的方法
//     * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
//     *
//     * @param obj
//     * @return
//     */
//    public static JSONArray getJsonArray(Object obj){
//
//        JsonConfig jsonConfig = new JsonConfig();
//        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
//
//        return JSONArray.fromObject(obj, jsonConfig);
//    }
//
//    /**
//     * 解析JSON字符串成一个MAP
//     *
//     * @param jsonStr json字符串，格式如： {dictTable:"BM_XB",groupValue:"分组值"}
//     * @return
//     */
//    public static Map<String, String> parseJsonStr(String jsonStr){
//
//        Map<String, String> result = new HashMap<String, String>();
//
//        JSONObject jsonObj = JsonParser.getJsonObject(jsonStr);
//
//        for(Object key : jsonObj.keySet()){
//            result.put((String)key, jsonObj.get(key).toString());
//        }
//        return result;
//    }
//
//    /**
//     * 解析Json字符串，自动转换成一个对象的集合
//     *
//     * @param jsonStr  如：[{tabId:'tab_tdjxjh',display:'true',index:'1'},{tabId:'tab_tdjxjh',display:'true',index:'1'},{tabId:'tab_tdjxjh',display:'true',index:'1'}]
//     * @param objClass  DdrzTabConf.class
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public static Collection parseJsonStrToCollection(String jsonStr, Class objClass){
//
//        if(StringUtils.isEmpty(jsonStr))
//            return null;
//
//        JSONArray jsonArray = JsonParser.getJsonArray(jsonStr);
//
//        return JSONArray.toCollection(jsonArray,objClass);
//    }
//    /**
//     * 解析Json字符串，自动转换成一个相应的对象
//     *
//     */
//    @SuppressWarnings("unchecked")
//    public static Object parseJsonStrToBean( String jsonStr,Class objClass){
//    	 JSONObject jsonObj = JsonParser.getJsonObject(jsonStr);
//    	 return JSONObject.toBean(jsonObj, objClass);
//    }
//    
//    @SuppressWarnings({ "unchecked", "deprecation" })
//    public static List<JSONObject> parseJsonArray(String jsonArrayStr){
//
//        if(StringUtils.isEmpty(jsonArrayStr))
//            return null;
//
//        List<Object> list = JSONArray.toList(getJsonArray(jsonArrayStr));
//        List<JSONObject> result = new ArrayList<JSONObject>();
//
//        for(Object obj : list){
//            JSONObject jsonObj = getJsonObject(obj);
//            result.add(jsonObj);
//        }
//
//        return result;
//    }
//
//    /**
//     * 把对象转换成为Json字符串
//     *
//     * @param obj
//     * @return
//     */
//    public static String convertObjectToJson(Object obj){
//        if(obj == null){
//            throw new IllegalAddException("对象参数不能为空。");
//        }
//        JSONObject jsonObject = JsonParser.getJsonObject(obj);
//        return jsonObject.toString();
//    }
//
//
//
//
//    public static void main(String[] args)
//    {
////        Map<String,String> linkedMap = new LinkedHashMap<String,String>();
////        linkedMap.put("bb","bbbbb");
////        linkedMap.put("22","22222");
////        linkedMap.put("11","11111");
////        linkedMap.put("aa","aaaaa");
////        for(String nextObj : linkedMap.keySet()){
////            System.out.println(nextObj + " = " + linkedMap.get(nextObj));
////        }
////        System.out.println(convertObjectToJson(linkedMap));
////
////        String str1 = "[{wideKnow:'chenmk',attrVal:'20080702~20080702'},{wideKnow:'chmk35',attrVal:'~20080702'}]";
////        System.out.println(parseJsonArray(str1));
////
//        String str = "801:'801_WF_SBTYSQD_GL,802_WF_SBTYSQD_GL,803_WF_SBTYSQD_GL',805:'805_WF_SQDYQ_GL',806:'806_WF_SQDSY_GL',807:'807_WF_XCKCD_GL',810:'810_WF_FSBGTZD_GL',811:'811_WF_SDGATZD_GL',812:'812_WF_ZDRWSQD_GL',813:'813_WF_ZDZTZD_GL',814:'814_WF_BGDYA_GL',901:'901_WF_GZP_DQDYZ_GL',902:'902_WF_GZP_DQDEZ_GL',903:'903_WF_GZP_BDZDDZY_GL',904:'904_WF_GZP_BDZSGYJQXD_GL',905:'905_WF_GZP_ECGZAQCSP_GL',906:'906_WF_GZP_DLXLDYZ_GL',907:'907_WF_GZP_DLXLDEZ_GL',908:'908_WF_GZP_DLXLDDZY_GL',909:'909_WF_GZP_DLXLSGYJQXD_GL',910:'910_WF_GZP_XLDLGZRWD_GL',911:'911_WF_GZP_DLDLDYZ_GL',912:'912_WF_GZP_DLDLDEZ_GL'";
//        str = "{"+str+"}";
//        Map<String,String> map = parseJsonStr(str);
//        
////
////        String tmp = "[{a:'chenmk',b:['EEE-DDD','MMMM']},{a:'chmk35',b:['ddddddddd','ccccccccc']}]";
////        List<JSONObject> result = parseJsonArray(tmp);
////
////        for(JSONObject jsonObj : result){
////            System.out.println(jsonObj.getString("a"));
////            Object obj = jsonObj.get("b");
////            System.out.println(jsonObj.getJSONArray("b").get(0));
////        }
//    }
//
//}
