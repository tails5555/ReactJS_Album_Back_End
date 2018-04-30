package net.kang.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Album;
import net.kang.domain.Photo;
import net.kang.service.AlbumService;
import net.kang.service.PhotoService;

@RestController
@CrossOrigin
@RequestMapping("main")
public class MainRestController {
	@Autowired PhotoService photoService;
	@Autowired AlbumService albumService;

	@GetMapping("albumList")
	public ResponseEntity<List<Album>> findAllAlbum(){
		return new ResponseEntity<List<Album>>(albumService.findAll(), HttpStatus.OK);
	}

	@GetMapping("album/{albumId}")
	public ResponseEntity<Album> findOneAlbum(@PathVariable("albumId") long albumId){
		Album album = albumService.findOne(albumId);
		if(album!=null)
			return new ResponseEntity<Album>(album, HttpStatus.OK);
		else
			return new ResponseEntity<Album>(new Album(), HttpStatus.NO_CONTENT);
	}

	@GetMapping("albumWithPhoto/{albumId}")
	public ResponseEntity<List<Photo>> findByAlbumPhotos(@PathVariable("albumId") long albumId) throws InterruptedException, ExecutionException, ServletException{
		List<Photo> photos = photoService.findByAlbumId(albumId).get();
		if(photos.size()>0) {
			return new ResponseEntity<List<Photo>>(photos, HttpStatus.OK);
		}else {
			return new ResponseEntity<List<Photo>>(photos, HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("photo/{photoId}")
	public ResponseEntity<?> photoView(@PathVariable("photoId") long id) throws ServletException, IOException {
		Photo photo = photoService.findOne(id);
		if(photo!=null) {
			HttpHeaders headers = new HttpHeaders();
			int infix=photo.getPhotoName().lastIndexOf('.');
			String photoPrefix = photo.getPhotoName().substring(infix + 1);
			photoPrefix.toLowerCase();
			switch(photoPrefix) {
				case "jpg" :
					headers.setContentType(MediaType.IMAGE_JPEG);
					break;
				case "jpeg" :
					headers.setContentType(MediaType.IMAGE_JPEG);
					break;
				case "png" :
					headers.setContentType(MediaType.IMAGE_PNG);
					break;
				default :
					headers.setContentType(MediaType.IMAGE_JPEG);
					break;
			}
			return new ResponseEntity<byte[]>(photo.getData(), headers, HttpStatus.OK);
		}
		return new ResponseEntity<String>("No Found", HttpStatus.NO_CONTENT);
	}
}
