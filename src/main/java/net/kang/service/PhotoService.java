package net.kang.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.kang.domain.Album;
import net.kang.domain.Photo;
import net.kang.repository.AlbumRepository;
import net.kang.repository.PhotoRepository;
import net.kang.util.Encryption;

@Service
public class PhotoService {
	private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
	@Autowired PhotoRepository photoRepository;
	@Autowired AlbumRepository albumRepository;

	public String fileNameEncryption(String fileName) {
		String[] file = fileName.split("."); // .을 통해서 파일 이름과 확장자를 구분시킨다.
		StringBuilder tmp = new StringBuilder();
		for(int k=0;k<file.length-1;k++) {
			tmp.append(Encryption.encrypt(file[k], Encryption.SHA256)); // 파일 이름을 암호화 설정한다.
			if(k!=file.length-1) {
				tmp.append("."); // 간혹 파일 이름 중에 .이 존재하는 경우(JavaScript의 경우), 이를 포함시켜 주도록 한다.
			}
		}
		tmp.append(file[file.length-1]); // 확장자는 그대로 설정한다.
		return tmp.toString();
	}

	public Photo findOne(long id) throws ServletException {
		Photo photo = photoRepository.findById(id).orElse(null);
		if(photo==null) throw new ServletException("존재하지 않는 사진 번호입니다.");
		return photo;
	}

	@Async("threadPoolTaskExecutor")
	public Future<String> photoUpload(MultipartFile[] photos, long albumId) throws InterruptedException, ServletException, IOException {
		Album album = albumRepository.findById(albumId).orElse(null);
		if(album==null) throw new ServletException("존재하지 않는 앨범 번호입니다.");
		for(MultipartFile file : photos) {
			if(file.getSize()<=0) continue;
			BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
			String name = Paths.get(file.getOriginalFilename()).getFileName().toString();
			log.info("Uploading Photo : {}", name);
			Photo photo = new Photo();
			photo.setPhotoName(this.fileNameEncryption(name));
			photo.setHeight(bufferedImage.getHeight());
			photo.setWidth(bufferedImage.getWidth());
			photo.setSize(file.getSize());
			photo.setUploadTime(LocalDateTime.now());
			photo.setData(file.getBytes());
			photo.setAlbum(album);
			photoRepository.save(photo);
			log.info("Uploading Photo is Successed!!! : {}", name);
		}
		Thread.sleep(2000L);
		return new AsyncResult<String>("Uploading Photos is Completed.");
	}

	@Async("threadPoolTaskExecutor")
	public Future<List<Photo>> findByAlbumId(long albumId) throws ServletException{
		Album album = albumRepository.findById(albumId).orElse(null);
		if(album==null) throw new ServletException("존재하지 않는 앨범 번호입니다.");
		log.info("Finding By Photo in Album : {}", album.getName());
		List<Photo> result = photoRepository.findByAlbum(album);
		if(result.size()>0) {
			log.info("Finding By Photo in Album Success : {}", album.getName());
			return new AsyncResult<List<Photo>>(result);
		}
		return new AsyncResult<List<Photo>>(new ArrayList<Photo>());
	}
}
