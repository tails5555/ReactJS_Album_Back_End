package net.kang.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kang.domain.Album;

public interface AlbumRepository extends JpaRepository<Album, Long>{

}
