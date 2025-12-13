package soat.project.fastfoodsoat.application.gateway;


import java.util.Optional;

import soat.project.fastfoodsoat.domain.order.Order;
import soat.project.fastfoodsoat.domain.order.OrderPublicId;
import soat.project.fastfoodsoat.domain.pagination.Pagination;
import soat.project.fastfoodsoat.domain.pagination.SearchQuery;


public interface OrderRepositoryGateway {
    Order create(Order order);
    Order update(Order order);
    Optional<Order> findByPublicId(OrderPublicId orderPublicId);
    Integer findLastOrderNumber();
    Pagination<Order> findAll(SearchQuery query);
    Pagination<Order> findAllForStaff(SearchQuery query);
}
