package org.irdigital.techmeup.api.controllers;

import lombok.RequiredArgsConstructor;
import org.irdigital.techmeup.api.dto.FileMetadataDTO;
import org.irdigital.techmeup.api.services.MediaService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Validated
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value="/attachments/{bucketId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<FileMetadataDTO> uploadFiles(
            @PathVariable("bucketId") String appId,
            @RequestPart("files") Flux<FilePart> filePartMono
    ) {
        return filePartMono
                .flatMap(it -> mediaService.upload(appId, it));
    }
}
