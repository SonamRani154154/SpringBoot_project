package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    public CategoryService categoryService;

    @Autowired
 public ProductService productService;

    @Autowired
    private UserService userService;

    @ModelAttribute
     public  void getuserDetails(Principal p, Model m){
        if(p!=null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user",userDtls);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys",allActiveCategory);
     }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/signin")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        m.addAttribute("categories",categories);
        m.addAttribute("products",products);
       m.addAttribute("paramValue",category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m){
        Product productById = productService.getProductById(id);
        m.addAttribute("product",productById);
        return "view_product";
    }


@PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
       String imageName= file.isEmpty() ?"default.jpg": file.getOriginalFilename();
       user.setProfileImage(imageName);
    UserDtls saveUser = userService.saveUser(user);
    if(!ObjectUtils.isEmpty(saveUser))
    {
      if(!file.isEmpty()){

          File saveFile=  new ClassPathResource("static/IMG").getFile();
          Path path=  Paths.get(saveFile.getAbsolutePath()+File.separator+"profile_img"+File.separator+file.getOriginalFilename());

          //System.out.println(path);
          Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

      }
        session.setAttribute("succMsg", "Register  successfully");
      } else{
        session.setAttribute("errorMsg","something wrong on server ");
    }
    return  "redirect:/register";
    }

// Forgot passWord Code
@GetMapping("/forgot-password")
public String showForgotPassword(){
return "forgot_password.html";
}

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,HttpSession session){
        UserDtls userByEmail = userService.getUserByEmail(email);
if(ObjectUtils.isEmpty(userByEmail))
{
    session.setAttribute("errorMsg","Invalid Email");
}
else {
    String resetToken = UUID.randomUUID().toString();
    userService.updateUserResetToken(email,resetToken);
    //Generate URl: http//localhost
    Boolean sendMail = CommonUtil.sendMail();

    if(sendMail){
        session.setAttribute("succMsg","Please Check Your Mail.. Password Resend link sent ");
    }
    else {
        session.setAttribute("errorMsg","Something Went Wrong on Server  | Email not send ");

    }
}
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(){
        return "reset_password.html";
    }

}
