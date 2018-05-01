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
import net.kang.domain.Suffix;
import net.kang.repository.AlbumRepository;
import net.kang.repository.PhotoRepository;
import net.kang.util.Encryption;

@Service
public class PhotoService {
	private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
	@Autowired PhotoRepository photoRepository;
	@Autowired AlbumRepository albumRepository;

	public Photo findOne(long id) throws ServletException {
		Photo photo = photoRepository.findById(id).orElse(null);
		if(photo==null) throw new ServletException("존재하지 않는 사진 번호입니다.");
		return photo;
	}

	@Async("photoNameEncoderExecutor") // 각 파일 별로 암호화를 하기 위한 비동기 주체를 통해 변환을 도와준다.
	public String fileNameEncryption(String fileName) throws InterruptedException {
		int infix=fileName.lastIndexOf('.'); // .을 이용해 파일 이름과 확장자를 구분한다.
		String fileSuffix = fileName.substring(infix, fileName.length()); // 확장자를 가져와서 붙인다.
		String filePrefix = Encryption.encrypt(fileName.substring(0, infix-1), Encryption.SHA256); // 파일 이름에 대해 SHA256 암호화 알고리즘을 이용해서 암호화를 진행한다.

		Thread.sleep(1000L); // 비동기에 대해 겹치지 않도록 1초 간격으로 쉬도록 한다.
		return filePrefix + fileSuffix;
	}

	@Async("photoFileUploadExecutor")
	public Future<String> photoUpload(MultipartFile[] photos, long albumId) throws InterruptedException, ServletException, IOException {
		Album album = albumRepository.findById(albumId).orElse(null);
		if(album==null) throw new ServletException("존재하지 않는 앨범 번호입니다.");
		for(MultipartFile file : photos) {
			if(file.getSize()<=0) continue;
			BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
			String name = Paths.get(file.getOriginalFilename()).getFileName().toString();
			int infix=name.lastIndexOf('.'); // .을 이용해 파일 이름과 확장자를 구분한다.
			String filePrefix = name.substring(0, infix); // 파일 이름을 파일 제목으로 지정한다.
			String fileSuffix = name.substring(infix+1, name.length()); // 확장자를 가져 온다.
			log.info("Uploading Photo : {}", name);
			Photo photo = new Photo();
			photo.setTitle(filePrefix); // 파일 제목에 대해 설정한다.
			photo.setSuffix(Suffix.valueOf(fileSuffix.toUpperCase())); // 파일 확장자에 대해 Enum을 통해 가져온다.
			photo.setPhotoName(this.fileNameEncryption(name)); // 사진 이름을 암호화하여 설정한다.
			photo.setHeight(bufferedImage.getHeight()); // 사진의 높이에 대해 지정한다.
			photo.setWidth(bufferedImage.getWidth()); // 사진의 너비에 대해 지정한다.
			photo.setSize(file.getSize()); // 사진의 용량을 저장한다.
			photo.setUploadTime(LocalDateTime.now()); // 업로드 시간을 현재로 지정한다.
			photo.setData(file.getBytes()); // 파일의 binary file을 가져와서 저장한다.
			photo.setAlbum(album); // 앨범은 앨범 번호를 통해 검색된 앨범으로 저장을 한다.
			photoRepository.save(photo); // 사진에 대해 저장을 한다.
			log.info("Uploading Photo is Successed!!! : {}", name);
		}
		Thread.sleep(2000L); // 비동기에 대해 겹치지 않도록 하기 위해 2초 간격으로 휴식을 한다.
		return new AsyncResult<String>("Uploading Photos is Completed.");
	}

	public List<Photo> findByAlbumId(long albumId) throws ServletException{
		Album album = albumRepository.findById(albumId).orElse(null);
		if(album==null) throw new ServletException("존재하지 않는 앨범 번호입니다.");
		log.info("Finding By Photo in Album : {}", album.getName());
		List<Photo> result = photoRepository.findByAlbum(album);
		if(result.size()>0) {
			log.info("Finding By Photo in Album Success : {}", album.getName());
			return result;
		}
		return new ArrayList<Photo>();
	}
}
