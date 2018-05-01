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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
		List<Photo> photos = photoService.findByAlbumId(albumId);
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
			switch(photo.getSuffix()) {
				case JPEG :
					headers.setContentType(MediaType.IMAGE_JPEG);
					break;
				case JPG :
					headers.setContentType(MediaType.IMAGE_JPEG);
					break;
				case PNG :
					headers.setContentType(MediaType.IMAGE_PNG);
					break;
				case GIF :
					headers.setContentType(MediaType.IMAGE_GIF);
					break;
			}
			return new ResponseEntity<byte[]>(photo.getData(), headers, HttpStatus.OK);
		}
		return new ResponseEntity<String>("No Found", HttpStatus.NO_CONTENT);
	}

	@PostMapping("album/{albumId}/upload")
	public ResponseEntity<String> photoUpload(@RequestParam("files") MultipartFile[] files, @PathVariable("albumId") long albumId){
		System.out.println(files.length);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
}
