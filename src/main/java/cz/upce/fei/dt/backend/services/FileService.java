package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.backend.configurations.S3Buckets;
import cz.upce.fei.dt.backend.entities.File;
import cz.upce.fei.dt.backend.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FileService {
    private final S3Buckets s3Buckets;
    private final S3Service s3Service;
    private final FileRepository fileRepository;

    public Stream<File> fetchFromBackEnd(Long contractId, Query<File, Void> query) {
        return fileRepository.findAllByContractId(contractId, VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
    }

    @Transactional
    public void saveFile(File file, InputStream inputStream) {
        fileRepository.save(file);
        s3Service.putObject(s3Buckets.getContract(), file.getPath(), inputStream, file.getSize());
    }

    @Transactional
    public void delete(File file) throws S3Exception {
        fileRepository.delete(file);
        s3Service.deleteObject(s3Buckets.getContract(), file.getPath());
    }

    public byte[] getFile(File file) throws IOException, S3Exception {
        return s3Service.getObject(s3Buckets.getContract(), file.getPath());
    }

    public void deleteAll(Long contractID) {
        Iterable<File> files = fileRepository.findAllByContractId(contractID);
        files.forEach(this::delete);
    }

}
