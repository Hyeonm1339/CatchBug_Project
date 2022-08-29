package com.catchbug.biz.product;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.catchbug.biz.vo.ImgVO;
import com.catchbug.biz.vo.ProductVO;

import net.sf.json.JSONArray;

@Controller
public class ProductController {
	private String uploadFolder = "/Users/hyeon1339/resources";

	@Autowired
	private ProductService productService;

	// 상품 등록
	@RequestMapping("/productRegister.do")
	public String ProductRegister(Model model) {

		model.addAttribute("maincategory", productService.getMainCategory());
		model.addAttribute("subCategory", JSONArray.fromObject(productService.getSubCategory()));
		return "admin/product_register";
	}

	@PostMapping("/insertProduct.do")
	public String InsertProduct(ProductVO vo, ImgVO ivo) {
		System.out.println("상품 등록 처리");
		productService.insertProduct(vo);
		int a = vo.getProduct_no();
		System.out.println(a);
		ivo.setProduct_no(a);

		productService.insertImg(ivo);

		return "redirect:productRegister.do"; // 기본은 포워드방식으로 이동
	}

	@PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<ImgVO>> productImgAjaxUpload(MultipartFile[] uploadFile, HttpServletRequest req) { // 여러개의 파일을 받을때를 대비
		System.out.println("이미지 업로드 에이작스 작동");

		/* 이미지 파일 체크 */
		for (MultipartFile multipartFile : uploadFile) {

			File checkfile = new File(multipartFile.getOriginalFilename());
			String type = null;

			try {
				type = Files.probeContentType(checkfile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			/* 이미지 파일이 아닐경우 널을 반환 */
//			if (!type.startsWith("image")) {
//
//				List<ImgVO> list = null;
//				return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
//
//			}

		}

		/* 이미지 파일을 저장할 경로 김현민 맥북 기준으로 작성 */
		String uploadFolder = "/Users/hyeon1339/CatchBugProject/src/main/webapp/resources/productImg";
		System.out.println(uploadFolder);

		/* 날짜 폴더 경로 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();

		String str = sdf.format(date);
//		String datePath = str.replace("-", File.separator);

		/* 폴더 생성 */
		File uploadPath = new File(uploadFolder, str);

		if (uploadPath.exists() == false) {
			uploadPath.mkdirs();
		}

		/* 이미저 정보 담는 객체 */
		List<ImgVO> list = new ArrayList();

		// 향상된 for
		for (MultipartFile multipartFile : uploadFile) {

			ImgVO vo = new ImgVO(); // 이미지 정보 객체 생성

			/* 파일 이름 */
			String uploadFileName = multipartFile.getOriginalFilename();

			vo.setFileName(uploadFileName);
			vo.setUploadPath(str);

			/* uuid 적용 파일 이름 파일이름 중복 방지 */
			String uuid = UUID.randomUUID().toString();
			vo.setUuid(uuid);

			uploadFileName = uuid + "_" + uploadFileName;

			/* 파일 위치, 파일 이름을 합친 File 객체 */
			File saveFile = new File(uploadPath, uploadFileName);

			/* 파일 저장 */
			try {
				multipartFile.transferTo(saveFile);

				BufferedImage bo_image = ImageIO.read(saveFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			list.add(vo);
		}

		ResponseEntity<List<ImgVO>> result = new ResponseEntity<List<ImgVO>>(list, HttpStatus.OK);

		return result;
	}

	@GetMapping("/display")
	public ResponseEntity<byte[]> getImage(String fileName) {
		System.out.println("이미지 출력작동");
		/* 이미지 경로 */
		File file = new File("/Users/hyeon1339/resources/" + fileName);

		ResponseEntity<byte[]> result = null;

		try {

			HttpHeaders header = new HttpHeaders();

			header.add("Content-type", Files.probeContentType(file.toPath()));

			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

}
