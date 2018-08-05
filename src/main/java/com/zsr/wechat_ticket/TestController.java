package com.zsr.wechat_ticket;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test")
@Controller
public class TestController {

	@RequestMapping("main")
	public String main(Model model) {
		model.addAttribute("user", "zhengshr");
		return "test";
	}
}
