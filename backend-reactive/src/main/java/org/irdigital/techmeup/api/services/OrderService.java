package org.irdigital.techmeup.api.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.irdigital.techmeup.api.thirdparty.VTEXConfig;
import org.irdigital.techmeup.api.dto.CreateOrderDTO;
import org.irdigital.techmeup.api.thirdparty.VTEXWebClient;
import org.irdigital.techmeup.domain.entities.Order;
import org.irdigital.techmeup.domain.exceptions.VTEXAnkaException;
import org.irdigital.techmeup.domain.repositories.OrderRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import pe.intercorpretail.anka.core.exception.model.AnkaErrorCode;
import pe.intercorpretail.anka.core.exception.model.AnkaException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "vtex")
public class OrderService {

    @Getter
    @Setter
    private Map<String, VTEXConfig> credentialsMap;

    private final Map<String, VTEXWebClient> webClients = new HashMap<>();

    private final OrderRepository orderRepository;


    public Mono<Void> createOrder(String tenantId, CreateOrderDTO createOrderDTO) {
         getClientFor(tenantId)//Voy a obtener cliente HTTP de conexión a mock vtex
                .findOrderById(createOrderDTO.getOrderId())//Voy a mock vtex a buscar el detalle de pedido
                .map(order -> Order.createFromVtex(tenantId, order))//Convierto el objeto de transporte a objeto de persistencia
                .flatMap(this.orderRepository::save)//Hago la persistencia
                .subscribe(this::onData, this::onError);
         return Mono.empty();
    }

    private void onData(Order order) {
        log.info("Persisted successfully order with id {} for tenant {}", order.getOrderId(), order.getTenantId());
    }

    private void onError(Throwable throwable) {
        if(throwable instanceof VTEXAnkaException) {
            VTEXAnkaException ankaException = (VTEXAnkaException) throwable;
            log.error("Error handling vtex order for tenant {} order {} with message {}", ankaException.getTenantId(), ankaException.getOrderId(), ankaException.getMessage());
        } else {
            log.info("Error {}", throwable.getMessage(), throwable);
        }
    }

    public Mono<Order> findOrderById(String tenantId, String orderId) {
        return orderRepository
                .findById(Order.createId(tenantId, orderId))
                .switchIfEmpty(Mono.error(new AnkaException(AnkaErrorCode.FGL_VFN_003, "El pedido numero "+orderId + " no existe")));
    }


    private VTEXWebClient getClientFor(String tenantId) {
        if(!webClients.containsKey(tenantId)) {
            VTEXConfig vtexConfig = credentialsMap.get(tenantId);
            webClients.put(tenantId, new VTEXWebClient(tenantId, vtexConfig.getUrl(), vtexConfig.getAppId(), vtexConfig.getAppSecret()));
        }
        return webClients.get(tenantId);
    }
}
