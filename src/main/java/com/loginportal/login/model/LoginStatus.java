package com.loginportal.login.model;

public class LoginStatus {

	private Long userID;
	private String loginStatusMessage;

	public LoginStatus() {
		super();
	}

	public LoginStatus(Long userID, String loginStatusMessage) {
		super();
		this.userID = userID;
		this.loginStatusMessage = loginStatusMessage;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getLoginStatusMessage() {
		return loginStatusMessage;
	}

	public void setLoginStatusMessage(String loginStatusMessage) {
		this.loginStatusMessage = loginStatusMessage;
	}

	@Override
	public String toString() {
		return "LoginStatus [userID=" + userID + ", loginStatusMessage=" + loginStatusMessage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loginStatusMessage == null) ? 0 : loginStatusMessage.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginStatus other = (LoginStatus) obj;
		if (loginStatusMessage == null) {
			if (other.loginStatusMessage != null) {
				return false;
			}
		} else if (!loginStatusMessage.equals(other.loginStatusMessage)) {
			return false;
		}
		if (userID == null) {
			if (other.userID != null) {
				return false;
			}
		} else if (!userID.equals(other.userID)) {
			return false;
		}
		return true;
	}

}
