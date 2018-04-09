package com.melot.data.change.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.melot.data.change.console.controller.annotation.PermessionLimit;

import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {
	@RequestMapping("/")
	@PermessionLimit(limit=false)
	public String index(Model model, HttpServletRequest request) {
		return "redirect:/normConfig";
	}
	
}
