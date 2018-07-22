package com.zsr.wechat_ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zsr.wechat_ticket.entity.Ticket;
import com.zsr.wechat_ticket.entity.User;

@RequestMapping("/admin")
@Controller
public class AdminController {

	static Logger logger = LoggerFactory.getLogger(TicketController.class);
	
	@Resource
	UserRepository repo;

	ObjectMapper mapper = new ObjectMapper();
	@PostConstruct
	public void proc() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		//populateTestUsers();
		populateTickets();
	}

	void populateTestUsers() {
		// add user
		List<User> users = repo.findByName(null, null, null);
		if(users.size() == 0) {
			repo.save(new User() {
				{
					setUserName("郑树锐");
					setStaffNumber("001");
					setGender("男");
					setEmail("zhengshr@inspur.com");
					setDepartment("测试验证部");
					setOffice("测试一处");
					setCity("衡水");
					setProvince("河北");
				}
			});
			repo.save(new User() {
				{
					setUserName("周庆飞");
					setStaffNumber("002");
					setGender("男");
					setEmail("zhouqf@inspur.com");
					setDepartment("测试验证部");
					setOffice("测试二处");
					setCity("济宁");
					setProvince("山东");
				}
			});
			repo.save(new User() {
				{
					setUserName("胥志泉");
					setStaffNumber("003");
					setGender("男");
					setEmail("xuzhiquanbj@inspur.com");
					setDepartment("产品工程部");
					setOffice("测试一处");
					setCity("莱芜");
					setProvince("山东");
				}
			});
		}
		
	}
	
	void populateTickets() {
		//add ticket
		repo.deleteAllTickets();
		Calendar cal = Calendar.getInstance();
		Date bookTime = cal.getTime();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		
		repo.addTicket(new Ticket() {
			{
				setStaffNumber("001");
				setBookTime(bookTime);
				setUseDate(fmt.format(cal.getTime()));
				setUseTime("18:00");			
			}
		});
		repo.addTicket(new Ticket() {
			{
				setStaffNumber("002");
				setBookTime(bookTime);
				setUseDate(fmt.format(cal.getTime()));
				setUseTime("18:00");
			}
		});
		repo.addTicket(new Ticket() {
			{
				setStaffNumber("003");
				setBookTime(bookTime);
				setUseDate(fmt.format(cal.getTime()));
				setUseTime("18:00");
			}
		});
		
		cal.add(Calendar.DAY_OF_MONTH, 1);
		repo.addTicket(new Ticket() {
			{
				setStaffNumber("001");
				setBookTime(bookTime);
				setUseDate(fmt.format(cal.getTime()));
				setUseTime("8:00");
			}
		});
		repo.addTicket(new Ticket() {
			{
				setStaffNumber("001");
				setBookTime(bookTime);
				setUseDate(fmt.format(cal.getTime()));
				setUseTime("16:00");				
			}
		});

	}
	
	@RequestMapping("main")
	public String mainAdmin(Model model) {
		model.addAttribute("username", "zhengshr");
		return "NewFile";
	}
	
	@RequestMapping(value="findUsers", method=RequestMethod.POST)
	public @ResponseBody List<User> findUsers(String isAll){
		if(isAll == null) {
			return new ArrayList<>();
		}
		
		return repo.findByName(null, null, null);
	}
	
	public static class StaffGroup {
		public List<String> staffs;
	}
	
	@RequestMapping(value="removeUsers", method=RequestMethod.POST)
	public @ResponseBody StaffGroup removeUsers(@RequestBody StaffGroup staffs) throws JsonProcessingException {
		//return staffs;
		List<String> sns = staffs.staffs;
		staffs.staffs = new ArrayList<>();
		for(String sn: sns) {
			repo.remove(sn);
			staffs.staffs.add(sn);
		}
		return staffs;
	}
	
	@RequestMapping(value="addUsers", method=RequestMethod.POST)
	public @ResponseBody List<User> addUsers(@RequestBody List<User> users){
		List<User> ret = new ArrayList<>();
		for(User u: users) {
			if(repo.findByNumber(u.getStaffNumber()) != null) {
				continue;
			}
			
			repo.save(u);
			ret.add(repo.findByNumber(u.getStaffNumber()));
		}
		return ret;
	}
	
	public class TicketFull{
		public List<Ticket> tickets;
		public Map<String, User> users;
	}
	
	@RequestMapping(value="getTickets", method=RequestMethod.POST)
	public @ResponseBody TicketFull findTickets(String startDate, String endDate){
		logger.debug("in findTicket, startData: {}, endDate: {}.", startDate, endDate);
		
		List<Ticket> retli = repo.findTickets(null, startDate, endDate);
		Map<String, User> users = new HashMap<>();
		for(Ticket t: retli) {
			String staff = t.getStaffNumber();
			if(!users.containsKey(staff)) {
				users.put(staff, repo.findByNumber(staff));
			}
		}
		TicketFull ret = new TicketFull();
		ret.tickets = retli;
		ret.users = users;
		return ret;
	}
}
