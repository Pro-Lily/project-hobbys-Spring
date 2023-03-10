package com.mycompany.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.webapp.dto.BoardDto;
import com.mycompany.webapp.dto.LikedDto;
import com.mycompany.webapp.dto.MemberDto;
import com.mycompany.webapp.dto.NeighborDto;
import com.mycompany.webapp.dto.PagerDto;
import com.mycompany.webapp.dto.ReplyDto;
import com.mycompany.webapp.service.SettingService;

@Controller
@RequestMapping("/setting")
public class SettingController {

	@Resource
	private SettingService service;

	private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

	@RequestMapping("/manager")
	public String manager(MemberDto memberdto, HttpSession session, Model model) {
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		memberdto.setMemail(sessionMemail);
		MemberDto member = service.sessionconnect(memberdto);
		model.addAttribute("member", member);
		return "redirect:/manager/content";
	}

	@RequestMapping("/content")
	public String content(MemberDto memberdto, HttpSession session, Model model) { //http://localhost:8080/teamproject
		
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		
		memberdto.setMemail(sessionMemail);
		MemberDto member = service.sessionconnect(memberdto);
		model.addAttribute("member", member);
		return "setting/content";
	}

	//?????? ??????
	@PostMapping("/nDelete") //void - jsp??? ???????????? ?????????
	public void nDelete(int nno, HttpServletResponse response) throws Exception { //?????? response.getWriter();?????? runtimeexception??? ???????????? ??????????????????

		//???????????? ???????????? ????????? ??????
		service.nDelete(nno);

		//JSON ??????
		JSONObject jsonObject = new JSONObject(); //????????? {} ??? JSONObject / ????????? ?????? - [] ??? JSONArray
		jsonObject.put("result", "success");
		String json = jsonObject.toString(); //{"result","success"}

		//?????? ?????????
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=utf-8"); //json ?????? ????????????
		out.println(json);
		out.flush();
		out.close();
	}

	//?????? ????????? ?????????
	@GetMapping("/myneighborlist")
	public String myneighborlist(@RequestParam(defaultValue = "1") int pageNo, Model model, HttpSession session) { 
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		String SessionMurl = (String) session.getAttribute("SessionMurl");
		//?????????
		int totalRows = service.getTotalMyRownList(sessionMemail);
		PagerDto pager = new PagerDto(sessionMemail, 3, 5, totalRows, pageNo);
		List<NeighborDto> list = service.getNighborList(pager);
		model.addAttribute("pager", pager);
		model.addAttribute("list", list);
		return "setting/myneighborlist";
	}
	
	//????????? ??????
	@RequestMapping("/mybloglist")
	public String mybloglist(@RequestParam(defaultValue = "1") int pageNo, HttpSession session, Model model) { //http://localhost:8080/teamproject
		//logger.info("??????");
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		String SessionMurl = (String) session.getAttribute("SessionMurl");
		
		//?????????
		int totalRows = service.getTotalMyRow(SessionMurl); //
		
		PagerDto pager = new PagerDto(SessionMurl, 5, 5, totalRows, pageNo);
		
		List<BoardDto> blogList = service.getBoardListPage(pager);
		//List<BoardDto> list = service.getBoardList(sessionMemail);
		
		model.addAttribute("pager", pager);
		model.addAttribute("blogList", blogList);
		
		return "setting/mybloglist";
	}
	
	@RequestMapping("/deleteBlog")
	public void deleteBlog(@RequestParam(value="chbox[]")List<String> chbox,  HttpSession session, HttpServletResponse response, BoardDto board) throws Exception {
		logger.info("chbox.length======>"+ chbox.size());
		
		String murl = (String) session.getAttribute("SessionMurl");
		int bno = 0;
		
		for(String i : chbox) {
			logger.info("chbox.value======>"+i);
			bno = Integer.parseInt(i);
			service.boardDelete(bno);
		}
		//JSON ??????
		JSONObject jsonObject = new JSONObject(); //????????? {} ??? JSONObject / ????????? ?????? - [] ??? JSONArray
		jsonObject.put("result", "success");
		String json = jsonObject.toString(); //{"result","success"}

		//?????? ?????????
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=utf-8"); //json ?????? ????????????
		out.println(json);
		out.flush();
		out.close();
	}

	/*?????? - ?????? ??????*/
	@RequestMapping("/mycommentlist")
	public String mycommentlist(@RequestParam(defaultValue="1") int pageNo, HttpSession session, Model model) { //http://localhost:8080/teamproject
		
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		String SessionMurl = (String) session.getAttribute("SessionMurl");
		
		//?????????
		int totalRows = service.getTotalReplyRows(SessionMurl); //
		PagerDto pager = new PagerDto(SessionMurl, 5, 5, totalRows, pageNo);
		List<ReplyDto> replyList = service.getReplyListPage(pager);
		
		model.addAttribute("pager", pager);
		model.addAttribute("replyList", replyList);
		
	
		return "setting/mycommentlist";
	}
	
	/*?????? - ?????? ?????? ????????? ??????,???????????? */
	@RequestMapping("/deleteReply")
	public void deleteReply(@RequestParam(value="chbox[]")List<String> chbox, HttpSession session, HttpServletResponse response, ReplyDto reply) throws Exception {
		
		logger.info("chbox.length======>"+ chbox.size());
		String murl = (String) session.getAttribute("SessionMurl");
		int rno = 0;
		
		for(String i : chbox) {
			logger.info("chbox.value======>"+i);
			rno = Integer.parseInt(i);
			service.replyDelete(rno);
		}
		//JSON ??????
		JSONObject jsonObject = new JSONObject(); //????????? {} ??? JSONObject / ????????? ?????? - [] ??? JSONArray
		jsonObject.put("result", "success");
		String json = jsonObject.toString(); //{"result","success"}

		//?????? ?????????
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=utf-8"); //json ?????? ????????????
		out.println(json);
		out.flush();
		out.close();
	}

	
	@RequestMapping("/delete")
	public String delete(HttpSession session, Model model) { //http://localhost:8080/teamproject
		logger.info("??????");
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		return "setting/delete";
	}
	
	@RequestMapping("/nicknamecheck")
	public String nicknamecheck(MemberDto memberdto, String mnickname, HttpSession session, Model model) { //http://localhost:8080/teamproject
		logger.info("??????");
		int row = service.checknickname(mnickname);
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		MemberDto member = service.sessionconnect(memberdto);
		if(row == 1) {
			model.addAttribute("result", "fail");
			model.addAttribute("member", member);
			return "setting/setting";
		}else {
			member.setMnickname(mnickname);
			model.addAttribute("member", member);
			return "redirect:/manager/content";
		}
		
	}
	
	@RequestMapping("/setting")
	public String setting(MemberDto memberdto, HttpSession session, Model model) { //http://localhost:8080/teamproject
		logger.info("??????");
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		logger.info(sessionMemail);
		memberdto.setMemail(sessionMemail);
		MemberDto member = service.sessionconnect(memberdto);
		model.addAttribute("member", member);
		
		return "setting/setting";
	}
	
	@RequestMapping("/imagechange")
	public String imagechange(MemberDto memberdto, HttpSession session, Model model) { //http://localhost:8080/teamproject
		logger.info("??????");
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		logger.info(sessionMemail);
		memberdto.setMemail(sessionMemail);
		MemberDto member = service.sessionconnect(memberdto);
		model.addAttribute("member", member);
		return "setting/imagechange";
	}
	
	@GetMapping("/photodownload")
	public void photodownload(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info(fileName);
		
		//????????? ???????????? ?????? ?????? ?????? ????????? ??????
		String saveFilePath = "C:/temp/projectimage/member/" + fileName;
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
	
	@RequestMapping("/photoenroll")
	public String photoenroll(MemberDto member, HttpSession session, Model model) throws Exception, IOException {	
		//memberdto.setMmyimage("winter.PNG");
		//model.addAttribute("member", member);
		logger.info("?????? :"+member.getMmyimage());
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		if(!member.getMphotoAttach().isEmpty()) {
			String originalFileName = member.getMphotoAttach().getOriginalFilename();
			String saveName = new Date().getTime() + "_" + originalFileName;
			logger.info("file name : "+ originalFileName);
			File dest = new File("C:/temp/projectimage/member/" + saveName);
			member.setMemail(sessionMemail);
			member.getMphotoAttach().transferTo(dest);
			member.setMmyimage(saveName);
			service.memberimageupdate(member);
		}else {
			logger.info("?????? :"+member.getMmyimage());
			model.addAttribute("error", "????????? ???????????? ??????");
		}
		return "redirect:/setting/content";
	}
	
	@RequestMapping("/photodelete")
	public String photodelete(MemberDto member, Model model, HttpSession session) {
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		
		member.setMemail(sessionMemail);
		member.setMmyimage("default.png");
		model.addAttribute("member", member);
		service.memberimageupdate(member);
		return "redirect:/setting/content";
	}
	
	@PostMapping("/updatenickintro")
	public String updatenickintro(MemberDto member, HttpSession session){
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		member.setMemail(sessionMemail);
		logger.info(member.getMemail());
		logger.info(member.getMintro());
		logger.info(member.getMnickname());
		service.membernickintroupdate(member);
		return "redirect:/setting/content";
	}
	
	/*@RequestMapping("/doublecheck")
	public String doublecheck(MemberDto member, HttpSession session) {
		
		return "setting/setting";
	}*/
	
	@RequestMapping("/mylikelist")
	public String mylikelist(@RequestParam(defaultValue = "1") int pageNo, HttpSession session, Model model) { //http://localhost:8080/teamproject
		//logger.info("??????");
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		int totalRows = service.getTotalMyLikeRow(sessionMemail); //
		PagerDto pager = new PagerDto(sessionMemail, 5, 5, totalRows, pageNo);
		Date time = new Date();
		
		List<LikedDto> list = service.getLikedListPage(pager);
		
		model.addAttribute("pager", pager);
		model.addAttribute("list", list);
		
		return "setting/mylikelist";
	}
	
	// ?????? ??????????????? ????????? ID, PW??? ???????????? ?????? ID, PW??? ???????????? ?????? // ??????
	@PostMapping("/deleteform")
	public void deleteform(String inputId, String inputPw, HttpSession session, HttpServletResponse response) throws IOException {
		String sessionMemail = (String) session.getAttribute("sessionMemail");
		MemberDto memberInfo = service.getMemberInfo(sessionMemail);
		JSONObject jsonObject = new JSONObject();
		
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(inputPw); // ???????????? ?????????
		logger.info("???????????? ????????????: " + encodedPassword);
		logger.info("DB?????? ????????? ????????? " + memberInfo.getMemail());
		logger.info("DB?????? ????????? ???????????? " + memberInfo.getMpassword());
		logger.info("???????????? ???????????? " + encodedPassword);
		logger.info("????????? ????????? " + inputId);
		
		String dbEmail = memberInfo.getMemail();
		if(inputId.equals(memberInfo.getMemail())) {
				logger.info("????????? ??????");
			
			if(passwordEncoder.matches(inputPw, memberInfo.getMpassword())) {
				logger.info("???????????? ??????");
				
				jsonObject.put("result", "success");
			}else {
				logger.info("???????????? ?????????");
				jsonObject.put("result", "fail");
			}	
		}else {
			logger.info("???????????? ?????????.");
			jsonObject.put("result", "fail");
		}	
		String json = jsonObject.toString();
		PrintWriter out = response.getWriter();
		response.setContentType("applecation/json; charset=utf-8");
		out.println(json);
		out.flush();
		out.close();
	}
	
	@PostMapping("/userDeleteAction")
	public void userDelete(String SessionMemail,HttpSession session, HttpServletResponse response) throws IOException {
		  logger.info("??????");
		  service.userDelete(SessionMemail);
		  
		  JSONObject jsonObject = new JSONObject(); jsonObject.put("result","success");
		  
		  String json = jsonObject.toString(); PrintWriter out = response.getWriter();
		  response.setContentType("applecation/json; charset=utf-8");
		  out.println(json); out.flush(); out.close(); session.invalidate();
	}
}
