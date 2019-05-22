package com.dha.dhabigdata.service;

import java.util.List;
import java.util.Map;

import com.dha.dhabigdata.entity.User;
import com.github.pagehelper.PageInfo;

public interface UserService
{
	
	public void addUser(User user);
	
	public User getUserById(Long userId);
	
	public List<Map> getUsersByUserCode(Map map);
	
	public PageInfo<Map> getUsersByUserCode(List<String> codeList);
	
	public void deleteUserById(User u);
	
	public void updateUserById(User u);
	
}
