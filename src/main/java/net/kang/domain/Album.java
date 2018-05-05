package net.kang.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Album {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;

	// 앨범 이름
	String name;

	// 앨범 내부에 있는 사진 목록들. 이는 OneToMany로 많이 안 쓰이기 때문에 LAZY 정책으로 설정하였다.
	@JsonIgnore
	@OneToMany(mappedBy="album", fetch=FetchType.LAZY)
	List<Photo> photoes;
}
