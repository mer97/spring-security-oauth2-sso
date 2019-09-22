package top.leemer.clientuser.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器。
 *
 * @author LEEMER
 * Create Date: 2019-09-21
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取用户列表。
     * 权限验证：
     *      当请求/api/v1/user/list接口时，判断该用户是否拥有“USER”权限。
     *
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/list")
    public JsonNode getUsers(){
        ObjectNode root = objectMapper.createObjectNode();

        // 模拟数据库数据
        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode node1 = objectMapper.createObjectNode();
        node1.put("username", "桂香");
        node1.put("age", 18);
        node1.put("gender", "女");
        ObjectNode node2 = objectMapper.createObjectNode();
        node2.put("username", "湘桂");
        node2.put("age", 21);
        node2.put("gender", "男");
        ObjectNode node3 = objectMapper.createObjectNode();
        node3.put("username", "凯凯");
        node3.put("age", 20);
        node3.put("gender", "男");
        arrayNode.add(node1);
        arrayNode.add(node2);
        arrayNode.add(node3);
        root.put("total", arrayNode.size());
        root.set("rows", arrayNode);

        return root;
    }

}
