package com.ddecker4.musicapp.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class MinioService {
	@Value("${minio.bucket}")
	private String bucket;

	private final S3Client s3Client;

	public MinioService(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	// upload file to MinIO storage
	// Param filename : what name of file will be in MinIO storage
	// Param content : contents of file to be uploaded
	// Return : none
	public void uploadFile(String filename, byte[] content) {
		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(bucket)
				.key(filename)
				.build(),
			RequestBody.fromBytes(content));
	}

	// download file with specified file name from MinIO storage
	// Param filename : name of file to be downloaded
	// Return : InputStream containing file content from MinIO storage 
	public InputStream downloadFile(String filename) {
	return s3Client.getObject(
		GetObjectRequest.builder()
			.bucket(bucket)
			.key(filename)
			.build());
	}

	// delete file with specified file name from MinIO storage
	// Param filename : name of file to be deleted
	// Return : none
	public void deleteFile(String filename) {
	s3Client.deleteObject(
		DeleteObjectRequest.builder()
			.bucket(bucket)
			.key(filename)
			.build());
	}

}