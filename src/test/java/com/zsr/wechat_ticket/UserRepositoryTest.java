package com.zsr.wechat_ticket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;
import static  org.hamcrest.Matchers.*;

import com.zsr.wechat_ticket.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

	@Autowired 
	UserRepository repo;
	
	@Test
	public void testAdd() {
		User user = new User();
		String sn = "43105";
		user.setEmail("zhengshr@inspur.com");
		user.setDepartment("测试验证部");
		user.setOffice("测试一处");
		user.setUserName("郑树锐");
		user.setStaffNumber(sn);
		
		repo.save(user);
		repo.setBindFlag(sn, "12nf8");
		user.setBindFlag("12nf8");
		User user1 = repo.findByNumber("43105");
		
		assertThat(user.getEmail(), equalTo(user1.getEmail()));
		assertThat(user.getDepartment(), equalTo(user1.getDepartment()));
		assertThat(user.getOffice(), equalTo(user1.getOffice()));
		assertThat(user.getUserName(), equalTo(user1.getUserName()));
		assertThat(user.getBindFlag(), equalTo(user1.getBindFlag()));
		assertThat(0, anything());
	}
}
