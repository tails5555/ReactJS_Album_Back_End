package net.kang.domain;

public enum Suffix {
	JPEG("jpeg"),
	JPG("jpg"),
	PNG("png"),
	GIF("gif");

	private String suffix;

	Suffix(String suffix){
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}
}
