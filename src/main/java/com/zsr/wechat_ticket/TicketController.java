package com.zsr.wechat_ticket;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zsr.wechat_ticket.entity.Ticket;
import com.zsr.wechat_ticket.entity.User;
import com.zsr.wechat_ticket.util.EmailSender;


@RequestMapping("/ticket")
@Controller
public class TicketController {
	static Logger logger = LoggerFactory.getLogger(TicketController.class);
	
	@Resource
	UserRepository repo;
	
	private ExecutorService execService =  Executors.newCachedThreadPool();

	@PostConstruct
	public void proc() {
	}
	
	
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public @ResponseBody User login(HttpServletRequest request, HttpSession session) throws Exception{
        
    	//String sn = request.getParameter("staffNumber");
    	String openId = request.getParameter("openid");
    	logger.debug("in login, openId: {}", openId);
    	//返回用户状态，分为未绑扎，半绑定，绑定等三个状态.
    	User user = repo.findByOpenId(openId);
       
    	return user;
    }
    
    @RequestMapping(value="/trybind", method = RequestMethod.POST)
    public @ResponseBody String tryBind(HttpServletRequest request, HttpSession session) throws Exception {
    	
    	String sn = request.getParameter("staffNumber");
    	String openId = request.getParameter("openid");
    	logger.debug("in bind, {},{}", sn, openId);
    	User user = repo.findByNumber(sn);
    	//已经绑定过了
    	if(user.getBindFlag() != null) {
    		return null;
    	}
    	
    	if(user.getOpenId() == null || !user.getOpenId().equals(openId)) {
    		repo.setOpenId(sn, openId);
    	}
    	
		//发送邮件，进行邮箱验证过程。返回验证码.
		int password = new Random().nextInt();
		password %= 1000000;
		String pwd = Integer.toString(password);
		repo.setPassword(sn, pwd);
		String emailMsg = "您绑定订餐小程序的验证码为" + pwd;
		execService.execute(new Runnable() {
			//可以发送失败，补救措施就是重新绑定一次.
			@Override
			public void run() {
	    		boolean bsucc = EmailSender.sendQQEmail(user.getEmail(), "来着订餐小程序的通知", emailMsg);
	    		if(bsucc) {
	    			repo.setSendTime(sn, new Date());
	    		}
			}
		});
		
		return pwd;   	
    	
    }
    
    @RequestMapping(value="/validatepassword", method = RequestMethod.POST)
    public @ResponseBody boolean validatePassword(HttpServletRequest request, HttpSession session) throws Exception{
    	String staffNumber = request.getParameter("staffNumber");
    	String openId = request.getParameter("openid");
    	String password = request.getParameter("password");
    	logger.debug("in bind, {}, {},{}", staffNumber, openId, password);
    	String pwd = repo.fetchPassword(staffNumber);
    	if(password.equals(pwd)) {
    		return true;
    	}else {
    		return false;
    	}
    }
 
    @RequestMapping(value="/bookticket", method = RequestMethod.POST)
    public @ResponseBody boolean bookTicket(HttpServletRequest request, HttpSession session,
    		@RequestBody List<Ticket> tickets) throws Exception {
    	String openId = request.getParameter("openid");
    	String password = request.getParameter("password");
    	logger.debug("in bind, {},{}", openId, password);
    	User user = repo.findByOpenId(openId);
    	if(user == null) {
    		return false;
    	}
    	for(Ticket ticket: tickets) {
			ticket.setStaffNumber(user.getStaffNumber());
	    	repo.addTicket(ticket);   		
    	}
    	
    	return true;
    }
    
    @RequestMapping(value="/findticket", method = RequestMethod.POST)   
    public List<Ticket> findTicketByStaff(HttpServletRequest request, HttpSession session) {
    	String openId = request.getParameter("openid");
    	//String sn = request.getParameter("staffNumber");
    	String startDate = request.getParameter("startDate");
    	String endDate = request.getParameter("endDate");
    	logger.debug("in findTicketByStaff, {}, {}, {}", openId, startDate, endDate);
    	User user = repo.findByOpenId(openId);
    	return repo.findTickets(user.getStaffNumber(), startDate, endDate);
    }
    
    
}