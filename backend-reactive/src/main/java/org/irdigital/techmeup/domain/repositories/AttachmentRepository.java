package org.irdigital.techmeup.domain.repositories;

import org.irdigital.techmeup.domain.entities.Attachment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository extends ReactiveCrudRepository<Attachment, UUID> {
}
