package com.firefox.center.oauth.auth.adminPassword;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @Author: sujie
 */
public class AdminPasswordAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	// ~ Instance fields
	// ================================================================================================
	private Object appId;
	private Object tenantId;
	private final Object principal;
	private Object credentials;

	public Object getManageAppIds() {
		return manageAppIds;
	}

	public void setManageAppIds(Object manageAppIds) {
		this.manageAppIds = manageAppIds;
	}

	private Object manageAppIds;

	public String getCenters() {
		return centers;
	}

	public void setCenters(String centers) {
		this.centers = centers;
	}

	private String centers;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
	 * will return <code>false</code>.
	 *
	 */
	public AdminPasswordAuthenticationToken(String appId, Integer tenantId, String username, String password, String manageAppIds, String centers) {
		super(null);
		this.appId = appId;
		this.tenantId = tenantId;
		this.principal = username;
		this.credentials = password;
		this.manageAppIds = manageAppIds;
		this.centers = centers;
		setAuthenticated(false);
	}

	/**
	 * This constructor should only be used by <code>AuthenticationManager</code> or
	 * <code>AuthenticationProvider</code> implementations that are satisfied with
	 * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
	 * authentication token.
	 *
	 * @param principal
	 * @param authorities
	 */
	public AdminPasswordAuthenticationToken(Object principal, Object credentials,
											Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true);
	}

	// ~ Methods
	// ========================================================================================================

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}
		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}

	public Object getAppId() {
		return appId;
	}

	public void setAppId(Object appId) {
		this.appId = appId;
	}

	public Object getTenantId() {
		return tenantId;
	}

	public void setTenantId(Object tenantId) {
		this.tenantId = tenantId;
	}
}
