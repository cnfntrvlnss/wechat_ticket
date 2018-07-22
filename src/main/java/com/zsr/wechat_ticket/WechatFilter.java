package com.zsr.wechat_ticket;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.zsr.wechat_ticket.util.HttpRequest;

public class WechatFilter implements Filter {
	static Logger logger = LoggerFactory.getLogger(WechatFilter.class);
	ObjectMapper objectMapper;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	String fetchRemoteOpenId(String code) throws Exception {
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
        
        return openid;
	}

	//code --> openId 的映射放到了session里，用作缓存.
	 String getOpenId(String code, HttpSession session) throws Exception {
	    	String lcode = (String) session.getAttribute("code");
	    	String openId;
	    	if(lcode != null && lcode.equals(code)) {
	    		openId = (String) session.getAttribute("openid");
	    	}else {
	    		openId = fetchRemoteOpenId(code);
	    		session.setAttribute("openid", openId);
	    		session.setAttribute("code", code);
	    	}
	    	return openId;
	 }
	 

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpSession session = ((HttpServletRequest) arg0).getSession();
		String code = arg0.getParameter("code");
		logger.debug("in doFilter, code: ", code);
		try {
			getOpenId(code, session);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		arg2.doFilter(arg0, arg1);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		objectMapper=new ObjectMapper();
		// 忽略json字符串中不识别的属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 忽略无法转换的对象 “No serializer found for class com.xxx.xxx”
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

	}

}
