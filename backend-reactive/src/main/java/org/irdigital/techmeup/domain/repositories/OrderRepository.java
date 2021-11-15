package org.irdigital.techmeup.domain.repositories;

import org.irdigital.techmeup.domain.entities.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, String> {

}
