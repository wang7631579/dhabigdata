package com.dha.dhabigdata.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long userId;
	private String userName;
	private String userCode;
	private List<String> codeList;

	/*CREATE TABLE `t_user` (
		  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
		  `userName` varchar(255) DEFAULT NULL,
		  `userCode` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`userId`)
	 ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
	*/
}
