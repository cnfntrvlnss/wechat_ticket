package com.zsr.wechat_ticket;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TicketController {

	@RequestMapping("main")
	String mainAdmin(Model model) {
		model.addAttribute("username", "zhengshr");
		return "NewFile";
	}
}