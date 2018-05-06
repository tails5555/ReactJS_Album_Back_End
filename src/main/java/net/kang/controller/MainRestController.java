package net.kang.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	// 앨범 목록에 대해서 AJAX를 통해 일반적으로 가져올 때 쓴다.
	@GetMapping("albumList")
	public ResponseEntity<List<Album>> findAllAlbum(){
		return new ResponseEntity<List<Album>>(albumService.findAll(), HttpStatus.OK);
	}

	// Client에서 앨범을 선택하면 이를 탐색해서 앨범 정보를 반환한다.
	@GetMapping("album/{albumId}")
	public ResponseEntity<Album> findOneAlbum(@PathVariable("albumId") long albumId){
		Album album = albumService.findOne(albumId);
		if(album!=null)
			return new ResponseEntity<Album>(album, HttpStatus.OK);
		else
			return new ResponseEntity<Album>(new Album(), HttpStatus.NO_CONTENT);
	}

	// 앨범에 있는 사진 목록들에 대해서 AJAX를 통해 오로지 사진의 정보만 가져올 때 쓴다. albumId은 Client에서 앨범을 선택하면 보내지는 값으로 앨범을 검색할 때 쓴다.
	@GetMapping("albumWithPhoto/{albumId}")
	public ResponseEntity<List<Photo>> findByAlbumPhotos(@PathVariable("albumId") long albumId) throws InterruptedException, ExecutionException, ServletException{
		List<Photo> photos = photoService.findByAlbumId(albumId);
		if(photos.size()>0) {
			return new ResponseEntity<List<Photo>>(photos, HttpStatus.OK);
		}else {
			return new ResponseEntity<List<Photo>>(photos, HttpStatus.NO_CONTENT);
		}
	}

	// photoId를 통해서 Photo 테이블에 들어 있는 사진 데이터에 대한 정보를 얻어와서 이를 BLOB에 저장된 데이터를 통해서 이미지를 보여준다.
	@GetMapping("photo/view/{photoId}")
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

	// 실제로 사진 하나 씩 업로딩을 하는 과정을 볼 수 있다. 본래 한 앨범에 여러 사진들을 업로드하든, 각 한 사진 씩 업로드하든 성능 차이는 크게 없기 때문에 이를 기반으로 작성하였다.
	@PostMapping(value="album/{albumId}/upload", consumes="multipart/form-data")
	public ResponseEntity<String> photoUpload(@RequestParam("file") MultipartFile file, @PathVariable("albumId") long albumId, OutputStream os){
		try {
			photoService.photoUpload(file, albumId, file.getBytes());
		} catch (InterruptedException e) { // 비동기에서 흔히 발생할 수 있는 인터럽트에 대해 예외 처리를 한다.
			return new ResponseEntity<String>("INTERRUPTED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ServletException e) { // 논리적 오류에 대해서 예외 처리를 한다.
			return new ResponseEntity<String>("SERVLET_ERROR", HttpStatus.BAD_REQUEST);
		} catch (IOException e) { // Photo Input, Output 중 문제 발생에 대해 예외 처리한다.
			return new ResponseEntity<String>("IO_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("UPLOAD_COMPLETE", HttpStatus.OK);
	}

	// 앨범 목록에서 각 사진에 있는 자세히 버튼을 클릭하는 경우에 한 사진의 정보를 가져와서 보여준다.
	@GetMapping(value="photo/info/{photoId}")
	public ResponseEntity<Photo> findOnePhoto(@PathVariable("photoId") long photoId) throws ServletException{
		Photo photo = photoService.findOne(photoId);
		if(photo!=null) return new ResponseEntity<Photo>(photo, HttpStatus.OK);
		else return new ResponseEntity<Photo>(photo, HttpStatus.NO_CONTENT);
	}

	// 한 사진에 대해서 삭제를 할 때 사진 ID를 가져와서 비동기적으로 삭제를 진행하게 된다.
	@DeleteMapping(value="photo/delete/{photoId}")
	public ResponseEntity<String> deleteById(@PathVariable("photoId") long photoId) throws InterruptedException{
		photoService.deleteById(photoId);
		return new ResponseEntity<String>("DELETE_COMPLETE", HttpStatus.OK);
	}

	// 여러 사진들에 대해서 삭제를 할 때 사진 ID들을 가져와서 비동기적으로 삭제를 진행을 하게 된다.
	@DeleteMapping(value="photo/deleteByIndexes")
	public ResponseEntity<String> photoDelete(@RequestBody List<Long> fileIndexes) throws InterruptedException{
		photoService.deleteByIndexes(fileIndexes);
		return new ResponseEntity<String>("PHOTOS_DELETE_COMPLETE", HttpStatus.OK);
	}

	// 아래에는 사진 업로드가 진행되면서 Progress Bar에 대해 구현을 할 수도 있음.
}
