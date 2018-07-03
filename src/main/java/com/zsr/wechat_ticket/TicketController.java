package com.zsr.wechat_ticket;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zsr.wechat_ticket.util.HttpRequest;


@Controller
public class TicketController {
	static Logger logger = LoggerFactory.getLogger(TicketController.class);
	
	ObjectMapper objectMapper;

	@RequestMapping("main")
	String mainAdmin(Model model) {
		model.addAttribute("username", "zhengshr");
		return "NewFile";
	}
	
	@PostConstruct
	public void proc() {
		objectMapper=new ObjectMapper();
		// 忽略json字符串中不识别的属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 忽略无法转换的对象 “No serializer found for class com.xxx.xxx”
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
	}
	
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public @ResponseBody String login(HttpServletRequest request) throws Exception{
        
    	String sn = request.getParameter("staffNumber");
    	String code = request.getParameter("code");
    	logger.debug("Start getSessionKey, {}:{}", code, sn);
        // 小程序唯一标识 (在微信小程序管理后台获取)    
        String wxspAppid = "wx104883f14055580c";    
        // 小程序的 app secret (在微信小程序管理后台获取)    
        String wxspSecret = "35ff437ddd0f5574091af8370a0115fb";    
        // 授权（必填）    
        String grant_type = "authorization_code";
              
      //请求参数
        String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type=" + grant_type;
        
        //发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
        //解析相应内容（转换成json对象）
        Map map = objectMapper.readValue(sr, Map.class);
        //JSONObject json = JSONObject.fromObject(sr);
        //获取会话密钥（session_key）
        String session_key = map.get("session_key").toString();
        //用户的唯一标识（openid）
        String openid = (String) map.get("openid");
        System.out.println("openid:" + openid);
        
    	return "success";
         
    }

}