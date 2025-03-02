package com.example.play_the_piano.s3file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.play_the_piano.global.exception.custom.Base64ConversionException;
import com.example.play_the_piano.global.exception.custom.InvalidBase64ExceptionException;
import com.example.play_the_piano.global.exception.custom.S3Exception;
import com.example.play_the_piano.s3file.customMultipartFile.CustomMultipartFile;
import com.example.play_the_piano.s3file.entity.ObjectEnum;
import com.example.play_the_piano.s3file.entity.S3File;
import com.example.play_the_piano.s3file.entity.S3FileRelation;
import com.example.play_the_piano.s3file.entity.TypeEnum;
import com.example.play_the_piano.s3file.repository.S3FileRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService {

	@Value("${aws.s3.bucketName}")
	private String bucketName;

	public final AmazonS3 amazonS3;

	public final S3FileRepository fileRepository;

	public String upload(MultipartFile file, String path, ObjectEnum objectEnum, Long objectId,
		TypeEnum typeEnum) {
		if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
			throw new S3Exception("유효하지 않은 파일 입니다.");
		}
		String fileUrl = this.uploadS3(file, path);
		S3File s3File = new S3File(fileUrl, typeEnum);
		fileRepository.createS3File(s3File);
		S3FileRelation s3FileRelation = new S3FileRelation(s3File, objectEnum, objectId);
		fileRepository.createS3FileRelation(s3FileRelation);
		return fileUrl;
	}


	private String uploadS3(MultipartFile file, String path) {
		try {
			return this.uploadS3File(file, path);
		} catch (IOException e) {
			throw new S3Exception("입출력 작업 중 문제가 발생하였습니다.");
		}
	}


	private String uploadS3File(MultipartFile file, String path) throws IOException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename.lastIndexOf(".") == -1) {
			throw new S3Exception("확장자명이 올바르지 않습니다.");
		}
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String s3Name = UUID.randomUUID().toString().substring(0, 10) + originalFilename;
		InputStream is = file.getInputStream();
		byte[] imageBytes = IOUtils.toByteArray(is);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(path + extension);
		objectMetadata.setContentLength(imageBytes.length);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Name,
				byteArrayInputStream, objectMetadata).withCannedAcl(
				CannedAccessControlList.PublicRead);
			amazonS3.putObject(putObjectRequest);
		} catch (Exception e) {
			throw new S3Exception("업로드 중 문제가 발생하였습니다.");
		} finally {
			byteArrayInputStream.close();
			is.close();
		}
		return amazonS3.getUrl(bucketName, s3Name).toString();
	}

	public List<String> getPostFile(ObjectEnum objectEnum, Long objectId, TypeEnum typeEnum) {
		return fileRepository.getFile(objectEnum, objectId, typeEnum);
	}

	public void deleteS3File(ObjectEnum objectEnum, Long objectId, TypeEnum typeEnum) {
		List<String> urls = getPostFile(objectEnum, objectId, typeEnum);
		if (!urls.isEmpty()) {
			for (String url : urls) {
				amazonS3.deleteObject(bucketName, url);
			}
		}
		fileRepository.deleteS3FileRelation(objectEnum,objectId);
		fileRepository.deleteFile(objectEnum, objectId, typeEnum);
	}

	public void softDeleteS3File(ObjectEnum objectEnum, Long objectId){
		fileRepository.softDeleteFile(objectEnum,objectId);
	}

	public List<String> extractBase64ImagesFromContent(String content) {
		String regex = "data:image/(png|jpeg|gif|bmp|svg\\+xml);base64,([A-Za-z0-9+/=]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		List<String> base64Images = new ArrayList<>();
		while (matcher.find()) {
			base64Images.add(matcher.group(0));
		}
		return base64Images;
	}

	public MultipartFile convertBase64ToMultipartFile(String base64Image) {
		String[] parts = base64Image.split(",");
		if (parts.length != 2) {
			throw new InvalidBase64ExceptionException("잘못된 Base64 문자열입니다.");
		}
		String base64Data = parts[1];
		String metadata = parts[0];
		String extension = metadata.split(";")[0].split("/")[1];
		byte[] imageBytes = Base64.decodeBase64(base64Data);

		try {
			String filename = UUID.randomUUID().toString() + "." + extension;
			return new CustomMultipartFile(imageBytes, filename);
		} catch (Exception e) {
			throw new Base64ConversionException("Base64를 MultipartFile로 변환하는 중 오류 발생했습니다.");
		}
	}

	public String updateContentWithImageUrls(String content, Map<String, String> base64ToUrlMap) {
		for (Map.Entry<String, String> entry : base64ToUrlMap.entrySet()) {
			content = content.replace(entry.getKey(), entry.getValue());
		}
		return content;
	}

	public String decodeText(List<String> base64Images, String content, ObjectEnum objectEnum,
		Long objectId) {
		Map<String, String> base64ToUrlMap = new HashMap<>();
		for (String base64Image : base64Images) {
			MultipartFile file = convertBase64ToMultipartFile(base64Image);
			String uploadedImageUrl = upload(file, "images/", objectEnum, objectId,
				TypeEnum.IMAGE);
			base64ToUrlMap.put(base64Image, uploadedImageUrl);
		}
		return updateContentWithImageUrls(content,
			base64ToUrlMap);
	}

}
