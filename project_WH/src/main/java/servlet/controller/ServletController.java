package servlet.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import servlet.service.MapService;
import servlet.service.ServletService;
import servlet.util.Util;

@Controller
public class ServletController {
	
	@Autowired
	private Util util;
	
	@Resource(name = "ServletService")
	private ServletService servletService;
	
	@RequestMapping(value = "/main.do")
	public String mainTest(ModelMap model) throws Exception {
		System.out.println("sevController.java - mainTest()");
		
		String str = servletService.addStringTest("START! ");
		model.addAttribute("resultStr", str);
		
		return "main/main";
	}
	
	@RequestMapping("/test.do")
	public String test() {
		return "test";
	}

	@RequestMapping("/testtwo.do")
	public String testTwo() {
		return "testTwo";
	}
	
	@RequestMapping("/maptest.do")
	public String mapTest(Model model) {
		
		List<Map<String, Object>> sdlist = servletService.sdList();
		model.addAttribute("sdlist", sdlist);
		
		return "mapTest";
	}
	
	@PostMapping("/maptest.do")
	public @ResponseBody List<Map<String, Object>> mapTest(@RequestParam("test") String test) {
		//System.out.println("선택 : " + test);
		
		List<Map<String, Object>> sgglist = servletService.sggList(test);		
		Map<String, Object> sdView = servletService.sdView(test);
		sgglist.add(sdView);
		System.out.println(sgglist);
		
		return sgglist;
	}
	
	/*
	 * @PostMapping("/sggSelect.do") public @ResponseBody List<Map<String, Object>>
	 * sggSelct(@RequestParam("sggcd") String sggcd){ //System.out.println("선택: " +
	 * sggcd);
	 * 
	 * List<Map<String, Object>> sggSelect = servletService.sggSelect(sggcd);
	 * 
	 * //범례 범주 뽑기
	 * 
	 * return sggSelect; }
	 */
	
	
	@PostMapping("/sggSelect.do")
	public @ResponseBody Map<String, Object> sggSelect(@RequestParam("sggName") String sggName){
		
		Map<String, Object> sggView = servletService.sggView(sggName);
		
		return sggView;
	}
	
	
	@PostMapping("/getData.do")
	public @ResponseBody Map<String, BigDecimal> dataList (@RequestParam("sggcd") String sggcd){
		
		//최대값, 최소값, 범례	
		Map<String, BigDecimal> bum = servletService.bum(sggcd);
		
		return bum;
	}
	
	@PostMapping("/equalInterval.do")
	public @ResponseBody List<Map<String, Object>> equalInterval(@RequestParam("sggcd") String sggcd){
		
		//sggcd 안의 bjd코드 조회		
		List<Map<String, Object>> sggSelect = servletService.sggSelect(sggcd);
		
		//bjd코드 별 사용량 조회		
		//사용량이 몇 레벨인지 조회
		
		List<Map<String, BigDecimal>> interval = util.equalInterval(sggcd);
		
		List<Map<String, Object>> list = new ArrayList();
		Map<String, Object> map;
		
		for (int i = 0; i < sggSelect.size(); i++) {
			BigDecimal use = (BigDecimal) sggSelect.get(i).get("usage");
			map = new HashedMap();
			
			for (int j = 0; j < interval.size(); j++) {
				int c = use.compareTo(interval.get(j).get("start"));
				int d = use.compareTo(interval.get(j).get("end"));
				
				if(j < interval.size() - 1 ) {
					if(c >= 0 && d < 0) {
						map.put("bjdcd", sggSelect.get(i).get("bjd_cd"));
						map.put("level", j + 1);
						break;
					}
				} else if(j == interval.size() - 1) {
					if(c > 0 && d <= 0) {
						map.put("bjdcd", sggSelect.get(i).get("bjd_cd"));
						map.put("level", j + 1);
						break;
					}
				}
			}
			list.add(map);
		}		
		
		System.out.println(list);
		
		return list;
	}
	
	
	@GetMapping("/fileUp.do")
	public String fileUp() {
		return "fileUp";
	}
	
	@PostMapping("/fileUp.do")
	public @ResponseBody String	fileUp(@RequestParam("upFile") MultipartFile upFile) throws IOException	{
		
		System.out.println(upFile.getName());
		System.out.println(upFile.getContentType());
		System.out.println(upFile.getSize());
		
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String,	Object>	m;
		
		InputStreamReader isr =	new InputStreamReader(upFile.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		
		while ((line =	br.readLine()) != null)	{
			m =	new HashMap<>();
			String[] lineArr = line.split("\\|");
			m.put("use_date", lineArr[0]);	//사용_년월	date
			m.put("addr", lineArr[1]);	//대지_위치	addr
			//m.put("newAddr", lineArr[2]);	//도로명_대지_위치	newAddr
			m.put("sgg_cd", lineArr[3]);	//시군구_코드	sigungu
			m.put("bjd_cd", lineArr[4]);	//법정동_코드	bubjungdong
			//m.put("addrCode", lineArr[5]);	//대지_구분_코드	addrCode
			//m.put("bun", lineArr[6]);	//번	bun
			//m.put("si",	lineArr[7]);	//지	si
			//m.put("newAddrCode", lineArr[8]);	//새주소_일련번호	newAddrCode
			//m.put("newAddr", lineArr[9]);	//새주소_도로_코드	newAddr
			//m.put("newAddrUnder", lineArr[10]);//새주소_지상지하_코드newAddrUnder
			//m.put("newAddrBun",	lineArr[11]);	//새주소_본_번	newAddrBun
			//m.put("newAddrBun2", lineArr[12]);	//새주소_부_번	newAddrBun2
			m.put("use_amount",	lineArr[13]);	//사용_량(KWh)	usekwh
			list.add(m);
		}
		
//		for (int i = 0; i < 20; i++) {
//			System.out.println(list.get(i));
//		}
		
		servletService.fileUp(list);
		
		br.close();
		isr.close();
		
		return "redirect:/fileUp";
	}
	
	
	
}
