package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import cz.upce.fei.dt.backend.configurations.S3Buckets;
import cz.upce.fei.dt.backend.entities.File;
import cz.upce.fei.dt.backend.repositories.FileRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private S3Buckets s3Buckets;
    @Mock
    private S3Service s3Service;
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @Mock
    private static File file;

    private static List<File> files;

    @BeforeAll
    static void beforeAll() {
        files = List.of(
                new File(),
                new File(),
                new File()
        );

        file = mock(File.class);
    }

    @Test
    void fetchFromBackEnd() {
        when(fileRepository.findAllByContractId(anyLong(), any())).thenReturn(new PageImpl<>(files));

        Stream<File> result = fileService.fetchFromBackEnd(1L, new Query<>());

        List<File> resultList = result.toList();
        assertEquals(resultList.size(), files.size());

        verify(fileRepository).findAllByContractId(eq(1L), any());
    }

    @Test
    void saveFile() {
        InputStream inputStream = mock(InputStream.class);

        when(file.getPath()).thenReturn("path");
        when(file.getSize()).thenReturn(32L);
        when(s3Buckets.getContract()).thenReturn("Contract");

        fileService.saveFile(file, inputStream);

        verify(fileRepository).save(any(File.class));
        verify(s3Service).putObject(anyString(), anyString(), any(InputStream.class), anyLong());
    }

    @Test
    void delete() {
        when(s3Buckets.getContract()).thenReturn("Contract");
        when(file.getPath()).thenReturn("path");

        fileService.delete(file);

        verify(fileRepository).delete(file);
        verify(s3Service).deleteObject(anyString(), anyString());
    }

    @Test
    void getFile() throws IOException {
        when(s3Buckets.getContract()).thenReturn("Contract");
        when(file.getPath()).thenReturn("path");

        fileService.getFile(file);

        verify(s3Service).getObject(anyString(), anyString());
    }

    @Test
    void deleteAll() {
        fileService.deleteAll(1L);

        verify(fileRepository).findAllByContractId(eq(1L));
    }
}