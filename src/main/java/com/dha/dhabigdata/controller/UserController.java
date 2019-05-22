package com.dha.dhabigdata.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dha.dhabigdata.entity.User;
import com.dha.dhabigdata.service.UserService;
import com.github.pagehelper.PageInfo;

@Controller
public class UserController {

	
	
	@Autowired
	UserService userService;

	@SuppressWarnings("all")
	@RequestMapping(value = "/addUser")
	public String addUser(Map map) {
		User user = new User();
		user.setUserCode("10001").setUserName("WPZ");
		userService.addUser(user);
		return "index";
	}

	@RequestMapping(value = "/testUserOtherMethod")
	@ResponseBody
	public void testUserOtherMethod() {
		User u = userService.getUserById(10L);
		System.out.println(u.getUserCode());
		Map userMap = new HashMap<String, Object>();
		userMap.put("name", u.getUserName());
		// userMap.put("code", u.getUserCode());
		List<Map> userList = userService.getUsersByUserCode(userMap);
		System.out.println(userList.size());
		// 分页
		String[] s = { "10001", "10002"};
		PageInfo<Map> userList2 = userService.getUsersByUserCode(Arrays.asList(s));
		System.out.println(userList2.toString());

	}

}
