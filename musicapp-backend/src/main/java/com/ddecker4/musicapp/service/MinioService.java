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

  public void uploadFile(String filename, byte[] content) {
    s3Client.putObject(
      PutObjectRequest.builder()
      .bucket(bucket)
      .key(filename)
      .build(),
      RequestBody.fromBytes(content)
    );
  }

  public InputStream downloadFile(String filename) {
    return s3Client.getObject(
      GetObjectRequest.builder()
        .bucket(bucket)
        .key(filename)
        .build()
    );
  }

  public void deleteFile(String filename) {
    s3Client.deleteObject(
      DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(filename)
        .build()
    );
  }

}