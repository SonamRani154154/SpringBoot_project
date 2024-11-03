package com.ecom.service.impl;

import com.ecom.model.Cart;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class orderServiceImpl implements OrderService {
@Autowired
    private ProductOrderRepository orderRepository;
@Autowired
private CartRepository cartRepository;

    @Override
    public ProductOrder saveOrder(Integer userid) {
        List<Cart> carts = cartRepository.findByUserId(userid);
         for(Cart cart:carts){

         }


        return null;
    }
}
