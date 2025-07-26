package com.wiilisten.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wiilisten.entity.User;

import lombok.Getter;


@Getter
public class UserDetail implements UserDetails {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//private User user;
	private String email;
	private String password;
	private String role;
	//change the argument from user to email,password and role
	public UserDetail(String email,String password,String role) {
		super();
		this.email = email;
		this.password = password;
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(role)));
	}

	@Override
	public String getPassword() {

		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
	/**
	 * The <code>getLoggedInUserId</code> is used to get the user id of the logged in user.
	 * @return
	 */
//	public User getLoggedInUser() {
//		return get;
//	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
