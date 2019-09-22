package top.leemer.ssoserver.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author LEEMER
 * Create Date: 2019-09-19
 */
@Controller
public class BaseController {

    /**
     * 跳转到登录页面。
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 跳转到首页。
     */
    @GetMapping("/")
    public String index(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "index";
    }

    /**
     * 获取当前登录用户。
    */
    @GetMapping("/principal")
    @ResponseBody
    public Principal getUserRidAuthority(Principal principal){
        return principal;
    }

}
