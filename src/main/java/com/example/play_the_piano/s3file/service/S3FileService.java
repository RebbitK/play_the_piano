package com.example.play_the_piano.s3file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.play_the_piano.global.exception.custom.S3Exception;
import com.example.play_the_piano.post.entity.Post;
import com.example.play_the_piano.s3file.entity.S3File;
import com.example.play_the_piano.s3file.entity.TypeEnum;
import com.example.play_the_piano.s3file.repository.S3FileRepository;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

	public String upload(MultipartFile file,String path, Post post, TypeEnum typeEnum) {
		if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
			throw new S3Exception("유효하지 않은 파일 입니다.");
		}
		String fileUrl = this.uploadS3(file,path);
		fileRepository.createS3File(new S3File(fileUrl,post,typeEnum));
		return fileUrl;
	}

	private String uploadS3(MultipartFile file,String path) {
		try {
			return this.uploadS3File(file,path);
		} catch (IOException e) {
			throw new S3Exception("입출력 작업 중 문제가 발생하였습니다.");
		}
	}


	private String uploadS3File(MultipartFile file,String path) throws IOException {
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

	public void removeImage(Long id){
		fileRepository.removeImage(id);
	}

	public void removeS3File(Long id){
		fileRepository.removeFile(id);
	}

	public void deleteS3File(Long id){
		fileRepository.deleteFile(id);
	}

}
