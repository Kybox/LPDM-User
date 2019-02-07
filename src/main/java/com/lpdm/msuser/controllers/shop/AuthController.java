package com.lpdm.msuser.controllers.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.security.cookie.CookieAppender;
import com.lpdm.msuser.security.jwt.auth.JwtGenerator;
import com.lpdm.msuser.security.jwt.auth.JwtUserBuilder;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import com.lpdm.msuser.services.shop.AuthService;
import com.lpdm.msuser.services.shop.SecurityService;
import com.lpdm.msuser.utils.shop.CustomModel;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthService authService;
    private final JwtGenerator jwtGenerator;
    private final SecurityService securityService;

    @Autowired
    public AuthController(AuthService authService,
                          JwtGenerator jwtGenerator,
                          SecurityService securityService) {

        this.authService = authService;
        this.jwtGenerator = jwtGenerator;
        this.securityService = securityService;
    }

    @GetMapping(value = "/shop/account/login")
    public ModelAndView loginPage(HttpServletRequest request) throws IOException {

        return CustomModel.getFor("/shop/fragments/account/login", request)
                .addObject("loginForm", new LoginForm());
    }

    @PostMapping(value = "/shop/account/login")
    public ModelAndView loginUser(@ModelAttribute LoginForm loginForm,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {

        log.info("LoginForm : " + loginForm);

        User user = null;
        String loginError = null;
        try{
            user = authService.loginUser(loginForm);
        }
        catch (FeignException e){
            loginError = e.getLocalizedMessage();
        }

        log.info("User : " + user);

        ModelAndView modelAndView;

        if(user != null){

            JwtUser jwtUser = JwtUserBuilder.build(user);
            CookieAppender.addToken(jwtGenerator.generate(jwtUser), response);

            modelAndView = CustomModel.getFor("redirect:/shop/account", request);
        }
        else {

            modelAndView = CustomModel.getFor("redirect:/shop/account/login", request)
                    .addObject("loginForm", new LoginForm())
                    .addObject("loginError", loginError);
        }

        return modelAndView;
    }

    @SuppressWarnings("Duplicates")
    @GetMapping(value = "/shop/account")
    public ModelAndView accountDefault(HttpServletRequest request) throws IOException {

        User user = securityService.getAuthenticatedUser(request);

        ModelAndView modelAndView;
        if(user != null){

            modelAndView = CustomModel.getFor("shop/fragments/account/account", request)
                    .addObject("accountContent", "cart");
        }
        else{

            modelAndView = CustomModel.getFor("/shop/fragments/account/login", request)
                    .addObject("loginForm", new LoginForm());
        }

        return modelAndView;
    }
}
