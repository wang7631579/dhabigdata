package com.dha.dhabigdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dha.dhabigdata.mapper.mysql.UserMapper;
import com.dha.dhabigdata.entity.User;
import com.dha.dhabigdata.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserMapper userMapper;

	@Override
	public void addUser(User user) {
		userMapper.addUser(user);
	}

	@Override
	public User getUserById(Long userId) {
		return userMapper.getUserById(userId);
	}

	@Override
	public List<Map> getUsersByUserCode(Map map) {
		return userMapper.getUsersByUserCode(map);
	}

	@Override
	public PageInfo<Map> getUsersByUserCode(List<String> codeList) {

		PageHelper.startPage(1, 1);
		List<Map> userList = userMapper.getUsersByUserCode2(codeList);
		PageInfo<Map> page = new PageInfo<>(userList);
		return page;
	}

	@Override
	public void deleteUserById(User u) {
		userMapper.deleteUserById(u);
	}

	@Override
	public void updateUserById(User u) {
		userMapper.updateUserById(u);
	}

}
