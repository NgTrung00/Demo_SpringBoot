package com.example.present.config;


import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.present.entity.JwtBlacklist;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthTokenFilter extends OncePerRequestFilter {
	
	private JwtProvider tokenProvider;

	private UserDetailsService userDetailsService;
	
	private JwtBlacklist jwtBlacklist;

	public JwtAuthTokenFilter(JwtBlacklist jwtBlacklist,JwtProvider tokenProvider,UserDetailsService userDetailsService) {
		this.jwtBlacklist = jwtBlacklist;
		this.tokenProvider=tokenProvider;
		this.userDetailsService=userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {

			String jwt = getJwtFromRequest(request);
			System.out.println(jwt+"aaaaa");
			if (jwt != null && jwtBlacklist.isBlacklisted(jwt)) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}else {
				if (jwt != null && tokenProvider.validateToken(jwt)) {
					String username = tokenProvider.getUserNameFromJwtToken(jwt);
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					if (userDetails != null) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authentication);

					}
				}
			}


		} catch (Exception ex) {
			System.out.println("failed on set user authentication");
		}

		filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}