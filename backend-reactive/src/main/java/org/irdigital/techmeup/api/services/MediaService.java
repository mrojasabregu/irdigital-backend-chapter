package org.irdigital.techmeup.api.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.irdigital.techmeup.api.dto.FileMetadataDTO;
import org.irdigital.techmeup.domain.entities.Attachment;
import org.irdigital.techmeup.domain.repositories.AttachmentRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.security.MessageDigest;
import java.util.UUID;

@Service
@Slf4j
@ConfigurationProperties(prefix = "attachments")
@RequiredArgsConstructor
public class MediaService {

    private final Storage storage;

    private final AttachmentRepository attachmentRepository;

    @Getter
    @Setter
    private String bucketName;

    public Mono<FileMetadataDTO> upload(String appId, FilePart filePart) {
        String contentType = filePart.headers().getContentType().toString();
        String fileName = filePart.filename();
        String attachmentName = String.format("%s/%s", appId, fileName);
        Mono<FileMetadataDTO> dataBufferMono = DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .map(bytes -> new FileMetadataDTO(attachmentName, contentType, (long) bytes.length, bytes))
                .flatMap(this::gcpUpload);
        dataBufferMono
                .map(this::createDatabaseAttachment)
                .flatMap(this.attachmentRepository::save)
                .subscribe();
        return dataBufferMono
                .subscribeOn(Schedulers.newParallel("thread-parallel-upload", 2));
    }

    private Attachment createDatabaseAttachment(FileMetadataDTO fileMetadataDTO) {
        return Attachment.builder()
                .attachmentId(UUID.randomUUID())
                .attachmentUri(fileMetadataDTO.getFileURI())
                .attachmentName(fileMetadataDTO.getName())
                .attachmentSize(fileMetadataDTO.getSize())
                .attachmentMime(fileMetadataDTO.getMimeType())
                .build();
    }

    public Mono<FileMetadataDTO> gcpUpload(FileMetadataDTO fileMetadata) {
        Mono<FileMetadataDTO> metadataMono = Mono.create((monoSink) -> {
            try {
                log.info("Uploading file {}", fileMetadata.getName());
                BlobInfo blobInfo = BlobInfo
                        .newBuilder(BlobId.of(bucketName, fileMetadata.getName()))
                        .setContentType(fileMetadata.getMimeType())
                        .build();
                Blob blob = storage.create(blobInfo, fileMetadata.getBytes());
                fileMetadata.setFileURI(blob.getMediaLink());
                fileMetadata.setSuccess(true);
                monoSink.success(fileMetadata);
            } catch(Throwable throwable) {
                monoSink.error(throwable);
            }
        });

        return metadataMono.onErrorResume(throwable -> {
            fileMetadata.setMessage("Error procesando archivo "+throwable.getMessage());
            fileMetadata.setSuccess(false);
            return Mono.just(fileMetadata);
        });
    }
}
