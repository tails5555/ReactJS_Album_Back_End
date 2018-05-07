package net.kang.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kang.domain.Album;
// Album 객체에 대한 Repository 생성.
public interface AlbumRepository extends JpaRepository<Album, Long>{

}
