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
	String name;

	@JsonIgnore
	@OneToMany(mappedBy="album", fetch=FetchType.LAZY)
	List<Photo> photoes;
}
