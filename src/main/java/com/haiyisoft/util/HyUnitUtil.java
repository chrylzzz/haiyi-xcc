package com.haiyisoft.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.haiyisoft.boot.IVRInit;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.HyUnitEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

/**
 * 海颐知识库工具类
 * Created By Chryl on 2023-12-07.
 */
@Slf4j
public class HyUnitUtil {

    public static JSONObject coreQuery(String queryText, String sessionId, String phone) {
        //package
        JSONObject param = coreQueryStruct(queryText, sessionId, phone);
        log.info("开始调用,海颐知识库接口入参:{}", JSON.toJSONString(param, JSONWriter.Feature.PrettyFormat));
        //invoke
//        String jsonStrResult = HttpClientUtil.doPostJsonForHaiyi(IVRInit.CHRYL_CONFIG_PROPERTY.getNgdCoreQueryUrl(), param.toJSONString());
        String jsonStrResult = HttpClientUtil.doPostJsonForHaiyi("http://172.20.200.3:8090/bot-service/open/query", param.toJSONString());
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
//        JSONObject context = new JSONObject();
//        context.put(XCCConstants.IVR_PHONE, phone);

        param.put("question", question);//客户问题
        param.put("sessionId", sessionId);//会话id
        param.put("phone", "15569695896");
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

    /**
     * 将海颐知识库返回的文本字段处理为 指令和播报的内容
     * 若无指令默认使用 YYSR
     *
     * @param hyUnitEvent
     * @return
     */
    public static HyUnitEvent convertText(HyUnitEvent hyUnitEvent) {
        String retKey;//指令
        String retValue;//播报内容
        String todoText = hyUnitEvent.getAnswer();
        log.info("convertText todoText: {}", todoText);
        if (StringUtils.isBlank(todoText)) {//话术为空
            retKey = XCCConstants.YYSR;
            retValue = XCCConstants.XCC_MISSING_ANSWER_TEXT;
        } else {
            if (!todoText.contains(XCCConstants.NGD_SEPARATOR)) {//不带#的话术
                retKey = XCCConstants.YYSR;
                retValue = todoText;
            } else {//带#的话术
                String[] split = todoText.split(XCCConstants.NGD_SEPARATOR);
                retKey = split[0];//指令
                if (StringUtils.containsAnyIgnoreCase(retKey, XCCConstants.RET_KEY_STR_ARRAY)) {//有指令
                    retValue = split[1];//内容
                } else {//无指令
                    retKey = XCCConstants.YYSR;
                    retValue = XCCConstants.XCC_MISSING_ANSWER_TEXT;
                }
            }
        }
        //转大写
        hyUnitEvent.setRetKey(retKey.toUpperCase());
        hyUnitEvent.setRetValue(retValue);
        log.info("convertText retKey: {} , retValue: {}", retKey, retValue);
        return hyUnitEvent;
    }
}
