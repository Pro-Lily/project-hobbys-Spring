package com.mycompany.webapp.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.MemberDao;
import com.mycompany.webapp.dto.MemberDto;

@Service
public class SigninService {
	@Resource
	private MemberDao memberdao;

	public MemberDto findMemail(String mphonenum) {
		MemberDto member = memberdao.selectByMphonenum(mphonenum);
		return member;
	}

	public int findMpassword(MemberDto member) {
		int row = memberdao.findmpassword(member);
		return row;
	}

	public int passwordResult(MemberDto member) {
		int row = memberdao.updatePassword(member);
		return row;
	}
}
