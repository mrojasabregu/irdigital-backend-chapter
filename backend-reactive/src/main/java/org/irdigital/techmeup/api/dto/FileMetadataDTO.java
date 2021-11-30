package org.irdigital.techmeup.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMetadataDTO {
    @NotEmpty

    private String fileURI;

    @NotEmpty
    @NonNull
    private String name;

    @NotEmpty
    @NonNull
    private String mimeType;

    @NotEmpty
    @NonNull
    private Long size;

    private Boolean success;

    private String message;

    @JsonIgnore
    @NonNull
    private byte[] bytes;
}