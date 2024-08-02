package cz.upce.fei.dt.beckend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private S3Client s3Client;
    @InjectMocks
    private S3Service s3Service;

    @Test
    void putObject() {
        s3Service.putObject("bucket", "key", mock(InputStream.class), 32L);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        PutObjectRequest putObjectRequest = captor.getValue();
        assertEquals("bucket", putObjectRequest.bucket());
        assertEquals("key", putObjectRequest.key());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getObject() throws IOException {
        ResponseInputStream<GetObjectResponse> responseInputStream = mock(ResponseInputStream.class);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);
        when(responseInputStream.readAllBytes()).thenReturn(new byte[0]);

        s3Service.getObject("bucket", "key");

        verify(s3Client).getObject(any(GetObjectRequest.class));
        verify(responseInputStream).readAllBytes();

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        GetObjectRequest getObjectRequest = captor.getValue();
        assertEquals("bucket", getObjectRequest.bucket());
        assertEquals("key", getObjectRequest.key());
    }

    @Test
    void deleteObject() {
        s3Service.deleteObject("bucket", "key");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(captor.capture());
        DeleteObjectRequest deleteObjectRequest = captor.getValue();
        assertEquals("bucket", deleteObjectRequest.bucket());
        assertEquals("key", deleteObjectRequest.key());
    }
}