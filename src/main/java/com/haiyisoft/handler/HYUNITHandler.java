package com.haiyisoft.handler;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.haiyisoft.util.HYUNITUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 海颐智能对话 HY-UNIT
 * HaiYi Understanding and Interaction Technology
 * Created By Chryl on 2023-12-07.
 */
@Slf4j
public class HYUNITHandler {

    /**
     * @param question
     * @param sessionId
     * @param phone
     */
    public static void handler(String question, String sessionId, String phone) {
        JSONObject result = HYUNITUtil.coreQuery(question, sessionId, phone);
        JSONArray resultJSONArray = result.getJSONArray("answers");
        if (resultJSONArray == null || resultJSONArray.isEmpty()) {

        } else {
            JSONObject answerJson = resultJSONArray.getJSONObject(0);
            String answer = answerJson.getString("answer");
            boolean contains = answer.contains("flow:");
//        if (contains) {//流程
            String convertAnswer = HYUNITUtil.convertAnswer(answerJson);
        }



/*
        {
            "chatId": "1732209943941091328",
                "question": "哦哦",
                "createTime": "2023-12-06 01:26:48",
                "channel": "web",
                "answers": [
            {
                "suggestionsSource": "",
                    "semanticId": "6ac1ae8ac9aa4ba7894a58c1716ae545",
                    "isKGTrigger": false,
                    "userAllowInputType": "VOICE",
                    "isFirstTrigger": true,
                    "callSceneNodeTitle": "",
                    "semanticSource": "T_USER_QA",
                    "robotVersion": "",
                    "isIntentNodeForKgTree": true,
                    "answerSource": "User",
                    "isAllowedInterrupt": true,
                    "callSceneNodeId": "none",
                    "score": 1,
                    "showResolveStatusLabel": true,
                    "hitType": "User",
                    "delay": 0,
                    "target_std": "哦哦哦",
                    "semantic_id": "6ac1ae8ac9aa4ba7894a58c1716ae545",
                    "answer": "flow:6ac1ae8ac9aa4ba7894a58c1716ae545",
                    "plainText": "flow:6ac1ae8ac9aa4ba7894a58c1716ae545",
                    "suggestions": [],
                "choices": [],
                "respond": [
                "尊敬的客户，您好，我是您的用电小助手，可以为您查询停电信息。",
                        "萨达萨达是",
                        "aaaaa",
                        "没结果"
            ],
                "status": "done"
            }
    ],
            "sessionId": "1111111",
                "hitKeyword": false,
                "userId": "1701826008197"
        }*/

    }

    public static void main(String[] args) {
        String[] arr = {"你好", ",", "我叫小赫兹"};

        JSONArray jsonArray = new JSONArray(arr);

    }

}
