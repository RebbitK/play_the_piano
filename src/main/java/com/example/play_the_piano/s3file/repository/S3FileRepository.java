package com.example.play_the_piano.s3file.repository;

import com.example.play_the_piano.s3file.entity.ObjectEnum;
import com.example.play_the_piano.s3file.entity.S3File;
import com.example.play_the_piano.s3file.entity.S3FileRelation;
import com.example.play_the_piano.s3file.entity.TypeEnum;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface S3FileRepository {

	void createS3File(S3File s3File);

	void createS3FileRelation(S3FileRelation s3FileRelation);

	List<String> getFile(ObjectEnum objectEnum, Long objectId, TypeEnum typeEnum);

	void deleteFile(ObjectEnum objectEnum, Long objectId, TypeEnum typeEnum);

	void deletedFile(ObjectEnum objectEnum, Long objectId);

	void deleteS3FileRelation(ObjectEnum objectEnum, Long objectId);
}
