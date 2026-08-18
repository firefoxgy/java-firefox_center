package com.firefox.center.oauth.auth.wx;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @Author: sujie
 */
public class WxAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	// ~ Instance fields
	// ================================================================================================

	private Object clientType;
	private Object version;
	private Object appId;
	private Object tenantId;
	private final Object principal;
	private Object credentials;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
	 * will return <code>false</code>.
	 *
	 */
	public WxAuthenticationToken(String clientType, String version, String appId, Integer tenantId, String thirdid, String password) {
		super(null);
		this.clientType = clientType;
		this.version = version;
		this.appId = appId;
		this.tenantId = tenantId;
		this.principal = thirdid;
		this.credentials = password;
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
	public WxAuthenticationToken(Object principal, Object credentials,
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

	public Object getClientType() {
		return clientType;
	}

	public void setClientType(Object clientType) {
		this.clientType = clientType;
	}

	public Object getVersion() {
		return version;
	}

	public void setVersion(Object version) {
		this.version = version;
	}
}
