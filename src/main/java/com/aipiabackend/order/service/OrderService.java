package com.aipiabackend.order.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.ORDER_ACCESS_FORBIDDEN;
import static com.aipiabackend.support.model.ErrorCodeMessage.ORDER_LINE_AMOUNT_NOT_MATCHED;
import static com.aipiabackend.support.model.ErrorCodeMessage.ORDER_NOT_FOUND;

import com.aipiabackend.member.model.MemberGrade;
import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.repository.OrderRepository;
import com.aipiabackend.order.service.dto.OrderCreateCommand;
import com.aipiabackend.order.service.dto.OrderLineCreateCommand;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.service.ProductService;
import com.aipiabackend.support.model.exception.AipiaDomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public Order create(OrderCreateCommand command) {
        List<OrderLineCreateCommand> orderLineCreateCommands = command.orderLines();

        // 가격 검증 및 재고 차감
        orderLineCreateCommands.forEach(orderLine -> {
            Product product = productService.findByIdWithPessimisticLock(orderLine.productId());

            // 요청된 주문 금액과 서버에서 계산한 주문 금액이 일치하는지 확인한다
            long actualOrderLineAmount = product.getPrice() * orderLine.productQuantity();
            if (orderLine.amount().compareTo(actualOrderLineAmount) != 0) {
                throw new AipiaDomainException(ORDER_LINE_AMOUNT_NOT_MATCHED);
            }

            // 상품의 재고를 차감한다.
            product.decreaseStock(orderLine.productQuantity());
            productService.save(product);
        });

        Order order = command.toOrder();
        return orderRepository.save(order);
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order findById(Long orderId) {
        return orderRepository.findByIdWithOrderLines(orderId)
            .orElseThrow(() -> new AipiaDomainException(ORDER_NOT_FOUND, "orderId='%s'".formatted(orderId)));
    }

    /**
     * 주문을 조회한다. 관리자는 모든 주문을 조회할 수 있고, 일반 회원은 본인의 주문만 조회할 수 있다
     */
    public Order retrieveOrder(Long orderId, Long memberId, MemberGrade grade) {
        Order order = findById(orderId);

        // 관리자가 아닌 경우, 본인의 주문인지 확인
        if (!grade.isAdmin() && !order.isOrderedBy(memberId)) {
            throw new AipiaDomainException(
                ORDER_ACCESS_FORBIDDEN,
                "orderId='%s', memberId='%s'".formatted(orderId, memberId)
            );
        }

        return order;
    }
}
