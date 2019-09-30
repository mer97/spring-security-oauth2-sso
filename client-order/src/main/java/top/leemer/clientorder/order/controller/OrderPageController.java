package top.leemer.clientorder.order.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * @author LEEMER
 * Create Date: 2019-09-21
 */
@Controller
@RequestMapping("/order")
public class OrderPageController {

    /**
     * 访问用户列表页面。
     * 权限验证：
     *      当请求/order/list接口时，判断该用户是否拥有“ORDER”权限。
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ORDER')")
    @GetMapping("/list")
    public String toOrderListPage(Principal principal, Model model){
        model.addAttribute("username", principal.getName());
        return "/web/order/list";
    }

}
