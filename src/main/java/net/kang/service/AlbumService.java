package net.kang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Album;
import net.kang.repository.AlbumRepository;

@Service
public class AlbumService {
	@Autowired AlbumRepository albumRepository;
	// 앨범의 모든 목록을 반환한다.
	public List<Album> findAll(){
		return albumRepository.findAll();
	}
	// 선택한 앨범에 대한 데이터의 결과를 반환한다.
	public Album findOne(long id) {
		Album album = albumRepository.findById(id).orElse(null);
		return album;
	}
}
