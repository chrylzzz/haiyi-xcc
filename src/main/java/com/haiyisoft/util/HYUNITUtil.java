package com.haiyisoft.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * 海颐知识库工具类
 * Created By Chryl on 2023-12-07.
 */
@Slf4j
public class HYUNITUtil {

    public static JSONObject coreQuery(String queryText, String sessionId, String phone) {
        //package
        JSONObject param = coreQueryStruct(queryText, sessionId, phone);
        log.info("开始调用,海颐知识库接口入参:{}", JSON.toJSONString(param, JSONWriter.Feature.PrettyFormat));
        //invoke
        String jsonStrResult = HttpClientUtil.doPostJsonForHaiyi(IVRInit.CHRYL_CONFIG_PROPERTY.getNgdCoreQueryUrl(), param.toJSONString());
        //res
        JSONObject parse = JSON.parseObject(jsonStrResult);
        log.info("结束调用,海颐知识库接口返回: {}", parse);
        return parse;
    }

    /**
     * 组装core query
     *
     * @param question
     * @param sessionId
     * @param phone
     * @return
     */
    public static JSONObject coreQueryStruct(String question, String sessionId, String phone) {
        JSONObject param = new JSONObject();
        JSONObject context = new JSONObject();
        context.put(XCCConstants.IVR_PHONE, phone);

        param.put("question", question);//客户问题
        param.put("sessionId", sessionId);//会话id
        param.put("phone", context);
        log.info("coreQueryStruct param : {}", param);
        return param;
    }

    public static String convertAnswer(JSONObject answerJson) {
        JSONArray respond = answerJson.getJSONArray("respond");
        Iterator<Object> iterator = respond.iterator();
        String convertAnswer = "";
        while (iterator.hasNext()) {
            String ansEle = iterator.next().toString();
            convertAnswer = convertAnswer + ansEle;
        }
        log.info("convertAnswer:{}", convertAnswer);
        return convertAnswer;
    }
}
