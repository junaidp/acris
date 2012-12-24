package sk.seges.acris.security.server.core.acl.domain.dto;

import sk.seges.acris.security.server.core.acl.domain.api.AclSid;

/**
 * The table ACL_SID essentially lists all the users in our systems
 */
public class AclSidDTO implements AclSid {

	private static final long serialVersionUID = 3753027336787453941L;

	private Long id;

	/**
	 * The distinction between two possibilities (user vs. role) is made by the value store in the principal column: -
	 * true indicates that the sid is a user - false means that the sid is a granted authority
	 */
	private boolean principal;

	/**
	 * In Spring Security, a "security id" (SID) is assigned to each user or role. This SID can be then used in an
	 * access control list (ACL) to specify which actions can the user with that SID perform on the desired objects. In
	 * fact, the SID may correspond to an user, a device or a system which can perform an action in the application, or
	 * it may correspond to a granted authority such as a role.
	 */
	private String sid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}