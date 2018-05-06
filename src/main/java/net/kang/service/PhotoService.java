package net.kang.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

	// 사진 ID를 이용해서 Client 측에 데이터를 보내준다.
	public Photo findOne(long id) throws ServletException {
		Photo photo = photoRepository.findById(id).orElse(null);
		if(photo==null) throw new ServletException("존재하지 않는 사진 번호입니다.");
		return photo;
	}

	// 각 파일 이름에 대해 암호화를 하여 반환을 하는 함수이다. 매개변수는 파일 이름이다.
	public String fileNameEncryption(String fileName) throws InterruptedException {
		int infix=fileName.lastIndexOf('.'); // .을 이용해 파일 이름과 확장자를 구분한다.

		String fileSuffix = fileName.substring(infix, fileName.length()); // 확장자를 가져와서 붙인다.
		String filePrefix = Encryption.encrypt(fileName.substring(0, infix-1), Encryption.SHA256); // 파일 이름에 대해 SHA256 암호화 알고리즘을 이용해서 암호화를 진행한다.

		Thread.sleep(1000L); // 비동기에 대해 겹치지 않도록 1초 간격으로 쉬도록 한다.
		return filePrefix + fileSuffix;
	}

	// 그림 파일의 데이터를 1개씩 받아서 각각 저장을 시키는 역할을 한다.
	@Async("photoFileUploadExecutor")
	@Transactional
	public void photoUpload(MultipartFile file, long albumId, byte[] fileByte) throws InterruptedException, ServletException, IOException {
		// 1단계. albumId를 이용해서 앨범의 존재 여부를 확인한다.
		log.info("Finding Album By Id : {}", albumId);
		Album album = albumRepository.findById(albumId).orElse(null);
		if(album==null) {
			log.error("Finding Album By Id Failure : {}", albumId);
			throw new ServletException("존재하지 않는 앨범 번호입니다.");
		}

		// 2단계. 파일의 Size가 0이거나 오류가 있는 파일에 대해서는 종료를 한다.
		if(fileByte.length<=0) return;

		// 3단계. 각 그림 파일에 대해 byte 배열을 받아서 BufferedImage를 이용해서 그림을 가져와 그림의 높낮이를 가져올 때 쓴다.
		// Java7 Ver에서 발표된 Try-With-Resource 예외기법을 이용하면 ByteArrayInputStream 객체에 대해서 입출력이 완수된다면 알아서 close()를 해주기 때문에 예외를 확실하게 처리할 수 있다.
		try(
			ByteArrayInputStream bais = new ByteArrayInputStream(fileByte);
		){
			BufferedImage bufferedImage = ImageIO.read(bais);
			// 4단계. 그림 본래 이름을 가져와서 파일 이름과 확장자를 구분시킨다.
			String name = file.getOriginalFilename();
			int infix=name.lastIndexOf('.'); // .을 이용해 파일 이름과 확장자를 구분한다.
			String filePrefix = name.substring(0, infix); // 파일 이름을 파일 제목으로 지정한다.
			String fileSuffix = name.substring(infix+1, name.length()); // 확장자를 가져 온다.

			// 5단계. 그림에 대해 정보를 가져와서 Setting을 한다.
			log.info("Uploading Photo : {}", name);
			Photo photo = new Photo();
			photo.setTitle(filePrefix); // 파일 제목에 대해 설정한다.
			photo.setSuffix(Suffix.valueOf(fileSuffix.toUpperCase())); // 파일 확장자에 대해 Enum을 통해 가져온다.
			photo.setPhotoName(this.fileNameEncryption(name)); // 사진 이름을 암호화하여 설정한다.
			photo.setHeight(bufferedImage.getHeight()); // 사진의 높이에 대해 지정한다.
			photo.setWidth(bufferedImage.getWidth()); // 사진의 너비에 대해 지정한다.
			photo.setSize(fileByte.length); // 사진의 용량을 저장한다. 단위는 Byte이다.
			photo.setUploadTime(LocalDateTime.now()); // 업로드 시간을 현재로 지정한다.
			photo.setData(fileByte); // 파일의 binary file을 가져와서 저장한다.
			photo.setAlbum(album); // 앨범은 앨범 번호를 통해 검색된 앨범으로 저장을 한다.
			photoRepository.save(photo); // 사진에 대해 저장을 한다.
			log.info("Uploading Photo is Successed!!! : {}", name);

			// 6단계. 비동기 작동에 대하여 올바르게 돌 수 있게 하기 위하여 Thread를 잠시 지연한다.
			Thread.sleep(2000L); // 비동기에 대해 겹치지 않도록 하기 위해 2초 간격으로 휴식을 한다.
		}catch(IOException e) { // 파일 입출력에 대한 예외를 여기서 처리한다.
			e.printStackTrace();
		}
	}

	// 앨범 ID를 이용해서 사진 목록을 가져오는 함수이다.
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

	@Async("photoDeleteExecutor")
	@Transactional
	public void deleteById(long photoId) throws InterruptedException {
		log.info("Delete By Photo ID : {}", photoId);
		photoRepository.deleteById(photoId);
		log.info("Delete By Photo ID Complete : {}", photoId);

		Thread.sleep(1000L); // 비동기에 대해 겹치지 않도록 하기 위해 2초 간격으로 휴식을 한다.
	}

	@Async("photoDeleteExecutor")
	@Transactional
	public void deleteByIndexes(List<Long> selectIndexes) throws InterruptedException{
		log.info("Delete By Select Indexes : {}", selectIndexes);
		photoRepository.deleteByIdIn(selectIndexes);
		log.info("Delete By Select Indexes Complete : {}", selectIndexes);

		Thread.sleep(2000L);
	}
}
