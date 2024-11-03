package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

     @Autowired
     private UserService userService;

     @Autowired
     private CategoryService categoryService;

@Autowired
     private CartService cartService;
@Autowired
private ProductService productService;

     @GetMapping("/")
     public String home(){
return ("user/home");
     }


     @ModelAttribute
     public  void getuserDetails(Principal p, Model m){
          if(p!=null) {
               String email = p.getName();
               UserDtls userDtls = userService.getUserByEmail(email);
               m.addAttribute("user",userDtls);
               Integer countCart = cartService.getCountCart(userDtls.getId());
               m.addAttribute("countCart", countCart);
          }
          List<Category> allActiveCategory = categoryService.getAllActiveCategory();
          m.addAttribute("categorys",allActiveCategory);

     }

@GetMapping("/addCart")
     public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
     Cart saveCart = cartService.saveCart(pid, uid);
     if(ObjectUtils.isEmpty(saveCart)){
        session.setAttribute("errorMsg","Product add to cart failed");
     }
     else {
          session.setAttribute("succMsg","product Addded to cart ");
     }
           return "redirect:/product/"+pid;
     }

     @GetMapping("/cart")
      public String loadCartPage(Principal p, Model m){
          UserDtls user= getLoggedInDetails(p);
          List<Cart> carts = cartService.getCartsByUser(user.getId());
          m.addAttribute("carts",carts);
          return"/user/cart";
      }

     private UserDtls getLoggedInDetails(Principal p) {
          String email= p.getName();
          UserDtls userDtls = userService.getUserByEmail(email);

          return userDtls;
     }
}
