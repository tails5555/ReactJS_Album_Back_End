package net.kang.domain;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Data
@Entity
public class Photo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;
	String photoName;

	@Column(name="photoWidth")
	int width;

	@Column(name="photoHeight")
	int height;

	@Column(name="photoSize")
	long size;

	LocalDateTime uploadTime;

	@Basic(fetch=FetchType.LAZY)
	@Lob
	@Column(name="photoData")
	byte[] data;
}
