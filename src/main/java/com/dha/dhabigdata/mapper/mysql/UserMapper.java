package com.dha.dhabigdata.mapper.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.dha.dhabigdata.entity.User;

@Mapper
public interface UserMapper {

public void addUser(User user);
	
	public User getUserById(Long userId);
	
	public List<Map> getUsersByUserCode(Map map);
	
	public List<Map> getUsersByUserCode2(List<String> codeList);
	
	public void deleteUserById(User user);
	
	public void updateUserById(User u);

}
