package net.kang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import net.kang.service.AlbumService;
import net.kang.service.PhotoService;

@RestController
public class MainRestController {
	@Autowired PhotoService photoService;
	@Autowired AlbumService albumService;

	// RestController 내부는 Front를 구상하면서 올릴 것입니다.
}
