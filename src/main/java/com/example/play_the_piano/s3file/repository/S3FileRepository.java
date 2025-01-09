package com.example.play_the_piano.s3file.repository;

import com.example.play_the_piano.s3file.entity.S3File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface S3FileRepository {

	void createS3File(S3File s3File);

	void removeImage(Long id);

	void removeFile(Long id);

	void deleteFile(Long id);

	String getPostFile(Long id);

	String getPostFileByPostId(Long id);

	String getPostImageByPostId(Long id);
}
