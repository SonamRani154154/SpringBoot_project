package com.ecom.util;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtil {
     public static  Boolean sendMail(String url, String email){

         return  false;
     }

      public static String generateUrl(HttpServletRequest request){
          String siteUrl = request.getRequestURL().toString();
       return siteUrl.replace(request.getServletPath(),"") ;

     }
}
