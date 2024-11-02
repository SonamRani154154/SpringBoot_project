package com.ecom.repository;

import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart,Integer> {

public  Cart findByProductIdAndUserId(Integer productId,Integer userId);



}
