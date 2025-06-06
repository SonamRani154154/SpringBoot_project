package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.OrderStatus;
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

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

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
         return "admin/index";
     }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m){
        List<Category> categories=categoryService.getAllCategory();
        m.addAttribute("categories",categories);
        return "admin/add_product";
    }


    @GetMapping("/category")
    public String category(Model m){
          m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public  String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
          String imageName = file!= null? file.getOriginalFilename():"default.jpg";
          category.setImageName(imageName);

        Boolean existCategory=  categoryService.existCategory(category.getName());
        if(existCategory){
            session.setAttribute("errorMsg","Category Name already  exists");
        }
        else{

            Category saveCategory = categoryService.saveCategory(category);
           if(ObjectUtils.isEmpty(saveCategory)){
               session.setAttribute("errorMsg"," Not Saved | internal server error");
              }
           else{
              File saveFile=  new ClassPathResource("static/IMG").getFile();
              Path path=  Paths.get(saveFile.getAbsolutePath()+File.separator+"Category"+File.separator+file.getOriginalFilename());

              //System.out.println(path);
              Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

               session.setAttribute("succMsg", "Saved successfully");

           }
        }

         return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if(deleteCategory){
            session.setAttribute("succMsg","Category delete success");
        }
        else {
            session.setAttribute("errorMsg","something wrong on server ");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
  public String loadEditCategory(@PathVariable int id, Model m){
        m.addAttribute("category",categoryService.getCategoryById(id));
        return "/admin/edit_category";
  }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
                                 HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(category)) {

            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(oldCategory);

        if (!ObjectUtils.isEmpty(updateCategory)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/IMG").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "Category" + File.separator
                        + file.getOriginalFilename());

                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("succMsg", "Category update success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveproduct = productService.saveproduct(product);
         if(!ObjectUtils.isEmpty(saveproduct))
         {

             File saveFile = new ClassPathResource("static/IMG").getFile();

             Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                     + image.getOriginalFilename());

             // System.out.println(path);
             Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
             session.setAttribute("succMsg","product saved Successfully ");
         }
          else{
               session.setAttribute("errorMsg", "Something Went wrong  on server ");
         }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m ){
        m.addAttribute("products",productService.getAllProducts());
        return "admin/products";
    }
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session){
        Boolean deleteProduct = productService.deleteProduct(id);
       if(deleteProduct){
           session.setAttribute("succMsg","delete product successfully");
       }
       else {
           session.setAttribute("errorMsg","Something went wrong on server ");
       }

        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model m){
    m.addAttribute("product",productService.getProductById(id));
    m.addAttribute("categories",categoryService.getAllCategory());
        return "admin/edit_product";
    }


    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session , Model m) throws IOException {

        if(product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg","invalid Discount ");
        }
        else {

        }
        Product updateProduct = productService.updateProduct(product, image);
        if(!ObjectUtils.isEmpty(updateProduct)){
            session.setAttribute("succMsg","update  product successfully");
        }
        else {
            session.setAttribute("errorMsg","Something went wrong on server ");
        }
        return "redirect:/admin/editProduct/"+product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m ){
        List<UserDtls> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateSts")
     public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session){

         Boolean f = userService.updateAccountStatus(id, status);

         if(f)
         {
             session.setAttribute("succMsg","Account Status  updated ");
         }
         else {
             session.setAttribute("errorMsg","Something went wrong on server ");
         }
         return "redirect:/admin/users";
     }



    @GetMapping("/orders")
    public String getAllOrders(Model m ){
        List<ProductOrder> allOrders = orderService.getAllOrders();
        m.addAttribute("orders", allOrders);
        return "/admin/orders";
    }



    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st,HttpSession session) {
        OrderStatus[] values = OrderStatus.values();

        String status=null;
        for(OrderStatus orderSt : values){
            if(orderSt.getId().equals(st)){
                status=orderSt.getName();
            }
        }
        Boolean updateOrder= orderService.updateOrderStatus(id,status);
        if(updateOrder){
            session.setAttribute("succMsg","Status Updated ");
        }
        else {
            session.setAttribute("errorMsg", "status Not updated ");
        }
        return "redirect:/admin/orders";
    }

}
