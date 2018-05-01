package net.kang.domain;

public enum Suffix {
	JPEG("JPEG"),
	JPG("JPG"),
	PNG("PNG"),
	GIF("GIF");

	private String suffix;

	Suffix(String suffix){
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}
}
