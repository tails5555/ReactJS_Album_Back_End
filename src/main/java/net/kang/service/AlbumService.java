package net.kang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Album;
import net.kang.repository.AlbumRepository;

@Service
public class AlbumService {
	@Autowired AlbumRepository albumRepository;
	public List<Album> findAll(){
		return albumRepository.findAll();
	}
	public Album findOne(long id) {
		Album album = albumRepository.findById(id).orElse(null);
		return album;
	}
}
