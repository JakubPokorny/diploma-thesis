package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.configurations.S3Buckets;
import cz.upce.fei.dt.beckend.entities.File;
import cz.upce.fei.dt.beckend.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    public int sizeInBackEnd(Long contractId, Query<File, Void> query) {
        return (int) fetchFromBackEnd(contractId, query).count();
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

    public Stream<File> findAllByContractId(Long contractId, int page, int pageSize) {
        return fileRepository.findAllByContractId(contractId, PageRequest.of(page, pageSize))
                .stream()
                .map(iFile -> File.builder()
                        .id(iFile.getId())
                        .name(iFile.getName())
                        .path(iFile.getPath())
                        .type(iFile.getType())
                        .size(iFile.getSize())
                        .created(iFile.getCreated())
                        .build());
    }

    public List<File> findAllByContactId(Long contractID) {
        return fileRepository.findAllByContractId(contractID);
    }

}
