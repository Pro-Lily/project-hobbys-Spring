package com.mycompany.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycompany.webapp.dto.BoardDto;
import com.mycompany.webapp.dto.MemberDto;
import com.mycompany.webapp.service.HomeService;

@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Resource
	private HomeService service;
	
	@RequestMapping("/")
	public String content(HttpSession session, Model model, String memail) { //http://localhost:8080/teamproject
		
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		logger.info("sessionMemail");
		if(sessionMemail==null) {
			session.setAttribute("SessionMnickname", "");
			session.setAttribute("SessionMemail", "");
			session.setAttribute("SessionMurl", "");
		}
		if(sessionMemail!=null) {
			MemberDto member = service.selectbyMemail(sessionMemail);
			model.addAttribute("member", member);
			logger.info(member.getMnickname());
			
			session.setAttribute("SessionMnickname", member.getMnickname());
			session.setAttribute("SessionMemail", member.getMemail());
			session.setAttribute("SessionMurl", member.getMurl());
		}
		return "home";
	}
	
	@RequestMapping("/elements")
	public String elements() { //http://localhost:8080/teamproject
	
		return "elements";
	}
	
	@RequestMapping("/sessionconnect")
	public String sessionconnect() { //http://localhost:8080/teamproject
		return "sessionconnect";
	}
	
	@PostMapping("/sessionconnectForm")
	public String sessionconnectForm(HttpServletRequest request, HttpSession session) { //http://localhost:8080/teamproject
		String value = request.getParameter("value");
		logger.info("value :" + value);
		session.setAttribute("sessionMemail", value);
		
		
		// ???????????? ???????????? ??????????????? ??? ??????

		String sessionMemail = (String) session.getAttribute("sessionMemail"); // ??????????????? ??????????????? ?????? ????????? ???????????? ????????? ??????
		logger.info("?????? ????????? sessionMemail??? " + sessionMemail);
		
		
		MemberDto member = service.getMemberInfo(sessionMemail); // ?????? ???????????? ????????? ???????????? ???????????? ????????? ????????? ????????????.
		logger.info("Member ????????? ??? : "  + member.toString());
		
		String UserUrl = member.getMurl(); // ????????? ????????? ?????? ????????? murl ????????? ??????
		logger.info("######################       member??? Muri = " + member.getMurl());
		session.setAttribute("UserUrl", UserUrl); // ????????? ??????????????? MURL??? ????????? ?????????.
	
		return "home";
	}
	
	// ?????? -  home??? ????????? Ranking ?????? 
	@RequestMapping("/homeRanking")
	public String homeRanking(Model model) {
		List<BoardDto> bRanking = service.getHomeRanking();
		model.addAttribute("board", bRanking);
		return "homeRanking";
	}
	
	@GetMapping("/boardphotodownload")
	public void boardphotodownload(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//????????? ???????????? ?????? ?????? ?????? ????????? ??????
		String saveFilePath = "C:/temp/projectimage/board/" + fileName;
		InputStream is = new FileInputStream(saveFilePath);
		
		//?????? HTTP ?????? ??????
		//1) Content-Type ?????? ??????(?????? ??????)
		ServletContext application = request.getServletContext();
		String fileType = application.getMimeType(fileName);
		response.setContentType(fileType);
		
		//??????????????? ?????? ?????? ?????? ??????
		//split???????????? ????????? ?????????

		//attachment; : ??????????????? ?????? ????????? ????????????(????????? ????????? ??? ????????? ?????????????????? ?????????, ????????? ??? ????????? ????????????
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
		
		//3)Content-Length ?????? ??????(??????????????? ????????? ????????? ??????)
		int fileSize = (int) new File(saveFilePath).length();
		response.setContentLength(fileSize);
		
		//?????? HTTP??? ??????(??????) ??????
		OutputStream os = response.getOutputStream();
		FileCopyUtils.copy(is, os); //??????????????? ??????
		os.flush();
		os.close();
		is.close();
	}
	
}