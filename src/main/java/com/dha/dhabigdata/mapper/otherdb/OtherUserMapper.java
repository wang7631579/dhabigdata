package com.dha.dhabigdata.mapper.otherdb;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtherUserMapper {

	public List<Map> getUsersByUserCode(Map map);

	public List<Map> getUsersByUserCode2(List<String> codeList);

}
