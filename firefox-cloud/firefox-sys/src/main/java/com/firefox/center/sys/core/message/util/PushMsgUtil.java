package com.firefox.center.sys.core.message.util;

import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.constant.WebsocketConst;
import com.firefox.center.sys.common.exception.FirefoxException;
import com.firefox.center.sys.common.system.vo.LoginUser;
import com.firefox.center.sys.common.util.oConvertUtils;
import com.firefox.center.sys.core.message.entity.SysMessage;
import com.firefox.center.sys.core.message.entity.SysMessageTemplate;
import com.firefox.center.sys.core.message.handle.enums.SendMsgStatusEnum;
import com.firefox.center.sys.core.message.service.ISysMessageService;
import com.firefox.center.sys.core.message.service.ISysMessageTemplateService;
import com.firefox.center.sys.core.message.websocket.WebSocket;
import com.firefox.center.sys.core.system.entity.SysAnnouncement;
import com.firefox.center.sys.core.system.entity.SysAnnouncementSend;
import com.firefox.center.sys.core.system.entity.SysUser;
import com.firefox.center.sys.core.system.mapper.SysAnnouncementMapper;
import com.firefox.center.sys.core.system.mapper.SysAnnouncementSendMapper;
import com.firefox.center.sys.core.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息生成工具
 */

@Component
@RequiredArgsConstructor
public class PushMsgUtil {

    private final ISysMessageService sysMessageService;
    private final WebSocket webSocket;
    private final SysUserMapper userMapper;
    private final ISysMessageTemplateService sysMessageTemplateService;
    private final SysAnnouncementMapper sysAnnouncementMapper;
    private final SysAnnouncementSendMapper sysAnnouncementSendMapper;

    /**
     * @param templateCode    消息模板码
     * @param map     消息参数
     * @param toUser  接收消息方
     */
    public boolean sendMessage(LoginUser user, String templateCode, Map<String, String> map, String toUser) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        SysMessage sysMessage = new SysMessage();
        if (sysSmsTemplates.size() > 0) {
            SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
            //模板标题
            String title = sysSmsTemplate.getTemplateName()==null?"":sysSmsTemplate.getTemplateName();
            //模板内容
            String content = sysSmsTemplate.getTemplateContent();

            if(map!=null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String str = "${" + entry.getKey() + "}";
                    title = title.replace(str, entry.getValue().toString());
                    content = content.replace(str, entry.getValue().toString());
                }
            }

            SysAnnouncement announcement = new SysAnnouncement();
            announcement.setTitile(title);
            announcement.setMsgContent(content);
            announcement.setSender(user.getUsername());
            announcement.setPriority(CommonConstant.PRIORITY_M);
            announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
            announcement.setSendStatus(CommonConstant.HAS_SEND);
            announcement.setSendTime(new Date());
            announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
            announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
            sysAnnouncementMapper.insert(announcement);
            // 2.插入用户通告阅读标记表记录
            String userId = toUser;
            String[] userIds = userId.split(",");
            String anntId = announcement.getId();
            for(int i=0;i<userIds.length;i++) {
                if(oConvertUtils.isNotEmpty(userIds[i])) {
                    SysUser sysUser = userMapper.getUserByName(userIds[i]);
                    if(sysUser==null) {
                        continue;
                    }
                    SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                    announcementSend.setAnntId(anntId);
                    announcementSend.setUserId(sysUser.getId());
                    announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                    sysAnnouncementSendMapper.insert(announcementSend);
                    JSONObject obj = new JSONObject();
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                    obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                    obj.put(WebsocketConst.MSG_ID, announcement.getId());
                    obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                    webSocket.sendOneMessage(sysUser.getId(), obj.toJSONString());
                }
            }

        }
        return false;
    }
}
