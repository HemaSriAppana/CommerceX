package com.practice.CommerceX.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

	  @PreAuthorize("hasRole('ADMIN')")
	    public String getAdminData() {

	        return "Sensitive admin data";
	    }
}
