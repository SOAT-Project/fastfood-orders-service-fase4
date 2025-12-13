package soat.project.fastfoodsoat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.mapper.OrderJpaMapper;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.ClientRepository;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.OrderRepository;
import soat.project.fastfoodsoat.infrastructure.persistence.jpa.repository.ProductRepository;
import soat.project.fastfoodsoat.domain.client.Client;
import soat.project.fastfoodsoat.domain.exception.NotFoundException;
import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.application.gateway.OrderRepositoryGateway;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.pagination.Pagination;
import soat.project.fastfoodsoat.domain.pagination.SearchQuery;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class OrderRepositoryGatewayImpl implements OrderRepositoryGateway {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public OrderRepositoryGatewayImpl(OrderRepository orderRepository,
                                      ProductRepository productRepository,
                                      ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Order create(final Order order) {
        final Map<Integer, ProductJpaEntity> products = createMapOfProductsById(order);

        final var client = order.getClientId() != null ?
                clientRepository.findById(order.getClientId().getValue())
                .orElseThrow(() -> NotFoundException.with(Client.class, order.getClientId())) : null;

        final OrderJpaEntity orderJpa = OrderJpaMapper.toJpa(order, products, client);

        return OrderJpaMapper.fromJpa(orderRepository.save(orderJpa));
    }

    @Override
    public Order update(final Order order) {

        final var clientJpa = order.getClientId() != null ?
                clientRepository.findById(order.getClientId().getValue())
                        .orElseThrow(() -> NotFoundException.with(Client.class, order.getClientId())) : null;

        final var productsMap = createMapOfProductsById(order);

        final var orderJpa = OrderJpaMapper.toJpa(order, productsMap, clientJpa);
        final var updatedOrderJpa = orderRepository.save(orderJpa);

        return OrderJpaMapper.fromJpa(updatedOrderJpa);
    }

    @Override
    public Optional<Order> findByPublicId(OrderPublicId orderPublicId) {
        return this.orderRepository.findOneByPublicId(orderPublicId.getValue())
                .map(OrderJpaMapper::fromJpa);
    }

    @Override
    public Integer findLastOrderNumber() {
        return this.orderRepository.findMaxOrderNumber()
                .orElse(0);
    }

    @Override
    public Pagination<Order> findAllForStaff(final SearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.Direction.fromString(query.direction()),
                query.sort()
        );

        if (isNull(query.terms())) {
            final Page<OrderJpaEntity> pageResult;

            pageResult = this.orderRepository.findAll(page);

            return new Pagination<>(
                    pageResult.getNumber(),
                    pageResult.getSize(),
                    pageResult.getTotalElements(),
                    pageResult.map(OrderJpaMapper::fromJpa).toList()
            );
        }

        final var terms = query.terms();
        var pageResult = tryFindByPublicId(terms, page);

        if (pageResult.isEmpty()) {
            pageResult = tryFindByOrderNumber(terms, page);
        }

        if (pageResult.isEmpty()) {
            pageResult = tryFindByProductName(terms, page);
        }

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(OrderJpaMapper::fromJpa).toList()
        );
    }

    @Override
    public Pagination<Order> findAll(final SearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage()
        );

        final Page<OrderJpaEntity> pageResult = this.orderRepository.findAllExcludingFinalizedAndSorted(page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(OrderJpaMapper::fromJpa).toList()
        );
    }

    private Page<OrderJpaEntity> tryFindByPublicId(final String terms, final PageRequest page) {
        try {
            final UUID uuid = UUID.fromString(terms);

            return orderRepository.findByPublicId(uuid, page);
        } catch (IllegalArgumentException e) {
            return org.springframework.data.domain.Page.empty(page);
        }
    }

    private Page<OrderJpaEntity> tryFindByOrderNumber(final String terms, final PageRequest page) {
        try {
            final Integer orderNumber = Integer.valueOf(terms);

            return orderRepository.findByOrderNumber(orderNumber, page);
        } catch (NumberFormatException e) {
            return org.springframework.data.domain.Page.empty(page);
        }
    }

    private Page<OrderJpaEntity> tryFindByProductName(final String terms, final PageRequest page) {
        return orderRepository.findDistinctByOrderProductsProductNameContainingIgnoreCase(terms, page);
    }

    private Map<Integer, ProductJpaEntity> createMapOfProductsById(final Order order) {
        return  order.getOrderProducts().stream()
                .map(p -> p.getProduct().getId().getValue())
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> productRepository.findById(id)
                                .orElseThrow(() -> NotFoundException.with(Product.class, new ProductId(id)))
                ));
    }
}
