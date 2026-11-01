package com.practice.CommerceX.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.CommerceX.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	private final AdminService ser;

	public AdminController(AdminService ser) {
		this.ser=ser;
	}
    @GetMapping("/dashboard")
    public String dashboard() {

        return "Admin Dashboard";
    }

    @GetMapping("/data")
    public String getAdminData() {

        return ser.getAdminData();
    }
}