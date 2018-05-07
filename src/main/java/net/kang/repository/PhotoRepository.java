package net.kang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kang.domain.Album;
import net.kang.domain.Photo;
// Photo 객체에 대해서 Repository 생성.
public interface PhotoRepository extends JpaRepository<Photo, Long>{
	// Album 객체를 통해서 사진 목록을 반환한다.
	List<Photo> findByAlbum(Album album);
	// 복수 선택을 통한 Photo를 삭제하도록 한다.
	void deleteByIdIn(List<Long> ids);
}
