package net.kang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kang.domain.Album;
import net.kang.domain.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long>{
	List<Photo> findByAlbum(Album album);
}
