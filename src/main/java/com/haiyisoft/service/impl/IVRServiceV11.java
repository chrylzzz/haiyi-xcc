package com.haiyisoft.service.impl;

import com.haiyisoft.chryl.client.XCCConnection;
import com.haiyisoft.chryl.ivr.DispatcherIVR;
import com.haiyisoft.constant.XCCConstants;
import com.haiyisoft.entry.ChannelEvent;
import com.haiyisoft.entry.HyUnitEvent;
import com.haiyisoft.entry.IVREvent;
import com.haiyisoft.entry.XCCEvent;
import com.haiyisoft.handler.HyUnitHandler;
import com.haiyisoft.handler.IVRHandler;
import com.haiyisoft.handler.XCCHandler;
import com.haiyisoft.service.IVRService;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * V11版本:配套海颐知识库
 * 欢迎语在海颐知识库
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class IVRServiceV11 implements IVRService {
    @Autowired
    private XCCConnection xccConnection;
    @Autowired
    private DispatcherIVR dispatcherIvr;

    @Override
    public void handlerChannelEvent(Connection nc, ChannelEvent channelEvent) {
        String state = channelEvent.getState();
        if (state == null) {
            log.error("state is null ");
        } else {

            //event
            IVREvent ivrEvent = IVRHandler.convertIVREvent(channelEvent);
            XCCEvent xccEvent = new XCCEvent();
            HyUnitEvent hyUnitEvent = new HyUnitEvent();
            //fs caller id
            String channelId = ivrEvent.getChannelId();
            //华为 caller id
            String icdCallerId = ivrEvent.getIcdCallerId();
            //来电号码
            String callerIdNumber = ivrEvent.getCidPhoneNumber();
            log.info("start this call channelId: {} , icdCallerId: {} , state:{} , IVREvent: {}", channelId, icdCallerId, state, ivrEvent);

            if (XCCConstants.CHANNEL_START.equals(state)) {
                //开始接管,第一个指令必须是Accept或Answer
                xccConnection.answer(nc, channelEvent);

                while (true) {

                    //xcc识别数据
                    String xccRecognitionResult = xccEvent.getXccRecognitionResult();

                    //获取指令和话术
                    hyUnitEvent = HyUnitHandler.handler(xccRecognitionResult, channelId, callerIdNumber, hyUnitEvent);

                    //处理是否已挂机
                    boolean handleHangup = XCCHandler.handleSomeHangup(xccEvent, channelId, nc, channelEvent);
                    if (handleHangup) {//挂机
                        //先存的IVR对话日志,这里挂机不需要单独处理
                        log.info("挂断部分");
                        break;
                    }

                    String  retKey = hyUnitEvent.getRetKey();
                    String  retValue = hyUnitEvent.getRetValue();

                    xccEvent = dispatcherIvr.doDispatch(nc, channelEvent, retKey, retValue, ivrEvent, hyUnitEvent, callerIdNumber);

                    log.info("revert ivrEvent data: {}", ivrEvent);
                }

            } else if (XCCConstants.CHANNEL_CALLING.equals(state)) {
                log.info("CHANNEL_CALLING this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_RINGING.equals(state)) {
                log.info("CHANNEL_RINGING this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_BRIDGE.equals(state)) {
                log.info("CHANNEL_BRIDGE this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_READY.equals(state)) {
                log.info("CHANNEL_READY this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_MEDIA.equals(state)) {
                log.info("CHANNEL_MEDIA this call channelId: {}", channelId);
            } else if (XCCConstants.CHANNEL_DESTROY.equals(state)) {
                log.info("CHANNEL_DESTROY this call channelId: {}", channelId);
            }

            //挂断双方
            xccConnection.hangup(nc, channelEvent);
            log.info("hangup this call channelId: {} ,icdCallerId: {}", channelId, icdCallerId);

            log.info("this call completed: {} , {}", ivrEvent, hyUnitEvent);
//            IVRHandler.afterHangupNotTransfer(ivrEvent, ngdEvent);

        }
    }

}
