package com.kakay.lectio.auth;

public class LectioKeystore {

	private String keystoreFile;
	private String keystorePassword;
	private String webtokenAlias;
	private String webtokenPassword;

	public LectioKeystore() {
	}

	public String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getWebtokenAlias() {
		return webtokenAlias;
	}

	public void setWebtokenAlias(String webtokenAlias) {
		this.webtokenAlias = webtokenAlias;
	}

	public String getWebtokenPassword() {
		return webtokenPassword;
	}

	public void setWebtokenPassword(String webtokenPassword) {
		this.webtokenPassword = webtokenPassword;
	}

}
