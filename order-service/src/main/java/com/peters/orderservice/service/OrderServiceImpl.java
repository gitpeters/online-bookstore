package com.peters.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peters.orderservice.controller.proxy.FeignProxy;

import com.peters.orderservice.controller.proxy.PaystackFeignProxy;
import com.peters.orderservice.dto.CustomResponse;
import com.peters.orderservice.dto.InitiatePaymentResponse;
import com.peters.orderservice.model.Cart;
import com.peters.orderservice.model.Order;
import com.peters.orderservice.model.Payment;
import com.peters.orderservice.repository.CartRepository;
import com.peters.orderservice.repository.OrderRepository;
import com.peters.orderservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final FeignProxy feignProxy;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaystackFeignProxy proxy;
    private final PaymentRepository paymentRepository;
    final String SECRET_KEY = "Bearer "+"sk_test_03487c78f5c63bb483e06bbfc634b8f2eab2b665";
    @Override
    public ResponseEntity<CustomResponse> addBookToCart(Long bookId, Long userId, int quantity) {
        ResponseEntity<CustomResponse> response = feignProxy.getBookById(bookId);
        log.info("Make a request call to the book service: {} ", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            CustomResponse customResponse = response.getBody();
            if (customResponse.getData() != null && customResponse.getData() instanceof Map) {
                Map<String, Object> bookData = (Map<String, Object>) customResponse.getData();
                if (bookData.containsKey("title") && bookData.containsKey("author") && bookData.containsKey("price")) {
                    String bookName = (String) bookData.get("title");
                    String authorName = (String) bookData.get("author");
                    BigDecimal bookPrice = new BigDecimal(String.valueOf(bookData.get("price")));

                    Cart cart = Cart.builder()
                            .bookId(bookId)
                            .userId(userId)
                            .bookName(bookName)
                            .authorName(authorName)
                            .bookPrice(bookPrice)
                            .quantity(quantity)
                            .subTotal(bookPrice.multiply(BigDecimal.valueOf(quantity)))
                            .dateAdded(LocalDate.now())
                            .build();

                    cartRepository.save(cart);

                    return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), cart, "Successfully added book to cart"));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "Could not find book by this id: " + bookId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"));
    }


    @Override
    public ResponseEntity<?> getAllCarts(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        log.info("fetch all carts for a user {} ", carts);

        BigDecimal totalAmount = BigDecimal.ZERO; // Initialize totalAmount to zero
        int totalQuantity = 0;
        for (Cart c : carts) {
            totalQuantity += c.getQuantity();
            totalAmount = totalAmount.add(c.getSubTotal()); // Update totalAmount
        }

        CustomResponse response = CustomResponse.builder()
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .data(carts)
                .message("Successfully retrieve all carts")
                .status("Successful")
                .totalNumberOfItems(carts.size())
                .build();

        if(carts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for user "));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomResponse> getCart(String cartId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if(cartOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for this id -> "+cartId));
        }
        Cart cart = cartOptional.get();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), cart, "Successful fetch cart item"));
    }

    @Override
    public ResponseEntity<CustomResponse> editCart(String cartId, int quantity) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if(cartOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for this id -> "+cartId));
        }
        Cart cart = cartOptional.get();
        cart.setQuantity(quantity);
        cart.setSubTotal(cart.getBookPrice().multiply(BigDecimal.valueOf(quantity)));
        cartRepository.save(cart);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), cart, "Successful updated cart item"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteCart(String cartId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if(cartOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for this id -> "+cartId));
        }
        Cart cart = cartOptional.get();
        cartRepository.delete(cart);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), cart, "Successful deleted item from cart"));
    }

    @Override
    public ResponseEntity<?> deleteAllCarts(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if(carts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for this id -> "+userId));
        }
        for(Cart cart: carts){
            cartRepository.delete(cart);
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Successful cleared all items from cart"));
    }

    @Override
    public ResponseEntity<?> checkOut(Long userId, String userEmail) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if(carts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND, "No cart found for this id -> "+userId));
        }

        ResponseEntity<InitiatePaymentResponse> paymentResponse = getInitiatePaymentResponseResponseEntity(userEmail, SECRET_KEY, carts);
        if (paymentResponse != null){
            this.deleteAllCarts(userId);
            return paymentResponse;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse(HttpStatus.BAD_REQUEST, "Something went wrong. Could not initiate payment"));
    }

    private ResponseEntity<InitiatePaymentResponse> getInitiatePaymentResponseResponseEntity(String userEmail, String SECRET_KEY, List<Cart> carts) {
        log.info("Initiating payment");
        Map<String, String> data = new HashMap<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (Cart c : carts) {
            totalQuantity += c.getQuantity();
            totalAmount = totalAmount.add(c.getSubTotal()); // Update totalAmount
        }
        Order order = Order.builder()
                .orderStatus("PENDING")
                .items(carts)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .totalNumberOfItems(carts.size())
                .orderDate(LocalDateTime.now())
                .build();

        BigDecimal amountToBePaid = totalAmount.multiply(BigDecimal.valueOf(100));

        data.put("email", userEmail);
        data.put("amount", String.valueOf(amountToBePaid));
        ResponseEntity<CustomResponse> initiatePaymentResponse = proxy.initiatePayment(SECRET_KEY, data);
        log.info("Initiating payment: {}", initiatePaymentResponse);
        if(initiatePaymentResponse.getStatusCode()==HttpStatus.OK){
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> response = mapper.convertValue(initiatePaymentResponse.getBody().getData(), Map.class);

            Payment payment = Payment.builder()
                    .paymentReferenceId((String) response.get("reference"))
                    .payeeEmail(userEmail)
                    .status("PROCESSING")
                    .build();
            paymentRepository.save(payment);

            order.setPayment(payment);
            orderRepository.save(order);

            InitiatePaymentResponse paymentResponse = InitiatePaymentResponse.builder()
                    .authorizationUrl((String) response.get("authorization_url"))
                    .paymentReference((String) response.get("reference"))
                    .build();
            return ResponseEntity.ok(paymentResponse);
        }
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> confirmPayment(String paymentReferenceId) {
        log.info("Calling confirmPayment endpoint with payment reference id: {} ", paymentReferenceId);
        ResponseEntity<CustomResponse> response = proxy.confirmPayment(SECRET_KEY, paymentReferenceId);
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mapResponse = mapper.convertValue(response.getBody().getData(), Map.class);
            Payment payment = paymentRepository.findByPaymentReferenceId(paymentReferenceId).orElseThrow();

            // Define a custom date-time formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            // Parse the string to LocalDateTime using the custom formatter
            LocalDateTime paymentDate = LocalDateTime.parse((String) mapResponse.get("paid_at"), formatter);
            int responseAmount = (Integer) mapResponse.get("amount");
            payment.setPaymentDate(paymentDate);
            payment.setPaymentMethod((String) mapResponse.get("channel"));
            payment.setAmount(BigDecimal.valueOf(responseAmount).divide(BigDecimal.valueOf(100)));
            payment.setStatus("PAID");
            paymentRepository.save(payment);

            Order order = orderRepository.findByPaymentReferenceId(paymentReferenceId).orElse(null);
            log.info("Retrieving order from db: {}",order);
            if (order != null) {
                order.setPayment(payment);
                order.setOrderStatus("PURCHASED");
                orderRepository.save(order);
                return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), payment, "Successfully completed payment"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse(HttpStatus.BAD_REQUEST, "Could not complete payment. Check payment reference id."));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse(HttpStatus.BAD_REQUEST, "Could not complete payment. Check payment reference id."));
    }


    private BigDecimal saveOrder(List<Cart> carts) {
        BigDecimal totalAmount = BigDecimal.ZERO; // Initialize totalAmount to zero
        int totalQuantity = 0;
        for (Cart c : carts) {
            totalQuantity += c.getQuantity();
            totalAmount = totalAmount.add(c.getSubTotal()); // Update totalAmount
        }
        Order order = Order.builder()
                .orderStatus("PENDING")
                .items(carts)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .totalNumberOfItems(carts.size())
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);
        return totalAmount;
    }
}
