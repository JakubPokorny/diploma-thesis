package cz.upce.fei.dt.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class S3Service {
    private final S3Client s3;

    public void putObject(String bucket, String key, InputStream inputStream, Long contentLength) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
    }

    public byte[] getObject(String bucketName, String key) throws IOException, S3Exception {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3.getObject(request).readAllBytes();
    }

    public void deleteObject(String bucketName, String key) throws S3Exception {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(deleteObjectRequest);
    }
}
