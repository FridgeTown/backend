package com.sparta.fritown.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3Service(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // MultipartFile을 임시 파일로 변환
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        try {
            // MultipartFile을 임시 파일로 저장
            file.transferTo(tempFile.toFile());

            // 파일명을 유니크하게 설정
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, tempFile.toFile()));

            // 업로드 후 임시 파일 삭제
            Files.delete(tempFile);

            return fileName;
        } catch (IOException e) {
            // 업로드 실패 시 로그 출력
            log.error("Error uploading file to S3: ", e);
            throw new IOException("Failed to upload file to S3", e);  // 예외 처리
        }
    }

    public String getFileUrl(String fileName) {
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        file.transferTo(convertedFile);
        return convertedFile;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }
}
