package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDtls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;
      private String name;
       private String mobileNumber;
       private String email;
        private String address;
         private String city;
          private String state;
           private String pincode;
            private String password;
             private String profileImage;
             private  String role;
private  Boolean isEnable;
 private  Boolean accountNonLocked;
 private Integer failedAttempt;
 private Date lockTime;

 private  String  resetToken;

}
