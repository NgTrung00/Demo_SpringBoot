package com.example.present.entity;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class JwtBlacklist {
	private List<String> blacklist = new ArrayList<>();

	private JwtBlacklist(List<String> blacklist) {
		super();
		this.blacklist = blacklist;
	}

	public List<String> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

	public void addToBlacklist(String token) {
		blacklist.add(token);
	}

	public boolean isBlacklisted(String token) {
		return blacklist.contains(token);
	}

	public void removeExpiredTokens() {
		// Remove expired tokens from blacklist
	}

	@Override
	public String toString() {
		return "JwtBlacklist [blacklist=" + blacklist + "]";
	}
	
}