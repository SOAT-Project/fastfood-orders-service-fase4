package soat.project.fastfoodsoat.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import soat.project.fastfoodsoat.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "public_id", nullable = false, columnDefinition = "uuid")
    private UUID publicId;

    @Column(name = "value", nullable = false, columnDefinition = "decimal(10,2)")
    private BigDecimal value;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientJpaEntity client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<OrderProductJpaEntity> orderProducts;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public OrderJpaEntity() {}

    public OrderJpaEntity(Integer id,
                          UUID publicId,
                          BigDecimal value,
                          Integer orderNumber,
                          OrderStatus status,
                          List<OrderProductJpaEntity> orderProducts,
                          Instant createdAt,
                          Instant updatedAt,
                          Instant deletedAt,
                          ClientJpaEntity client) {
        this.id = id;
        this.publicId = publicId;
        this.value = value;
        this.orderNumber = orderNumber;
        this.status = status;
        this.orderProducts = orderProducts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.client = client;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClientJpaEntity getClient() {
        return client;
    }

    public void setClient(ClientJpaEntity client) {
        this.client = client;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<OrderProductJpaEntity> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProductJpaEntity> orderProducts) {
        if (orderProducts == null) return;

        this.orderProducts = orderProducts;
    }

}
