package org.irdigital.techmeup.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("attachments")
public class Attachment implements Persistable<UUID> {
    @Id
    private UUID attachmentId;
    private String attachmentHash;
    private String attachmentName;
    private String attachmentMime;
    private Long attachmentSize;
    private String attachmentUri;

    @Override
    public UUID getId() {
        return attachmentId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
