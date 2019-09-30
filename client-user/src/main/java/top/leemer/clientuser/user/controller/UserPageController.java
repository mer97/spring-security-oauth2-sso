package top.leemer.clientuser.user.controller;

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
@RequestMapping("/user")
public class UserPageController {

    /**
     * 访问用户列表页面。
     * 权限验证：
     *      当请求/user/list接口时，判断该用户是否拥有“USER”权限。
     *
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/list")
    public String toUserListPage(Principal principal, Model model){
        model.addAttribute("username", principal.getName());
        return "/web/user/list";
    }

}
