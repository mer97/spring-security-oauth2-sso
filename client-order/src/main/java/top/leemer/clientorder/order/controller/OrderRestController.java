package top.leemer.clientorder.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LEEMER
 * Create Date: 2019-09-21
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderRestController {

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取订单列表。
     * 权限验证：
     *      当请求/api/v1/order/list接口时，判断该用户是否拥有“ORDER”权限。
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ORDER')")
    @GetMapping("/list")
    public JsonNode getUsers(){
        ObjectNode root = objectMapper.createObjectNode();

        // 模拟数据库数据
        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("productName", "真知棒250支装");
        node.put("number", 1);
        node.put("price", 98.8);
        node.put("username", "桂香");
        ObjectNode node2 = objectMapper.createObjectNode();
        node2.put("productName", "十八子菜刀");
        node2.put("number", 1);
        node2.put("price", 39);
        node2.put("username", "凯凯");
        ObjectNode node3 = objectMapper.createObjectNode();
        node3.put("productName", "AHT防辐射眼镜");
        node3.put("number", 1);
        node3.put("price", 288);
        node3.put("username", "湘桂");
        ObjectNode node4 = objectMapper.createObjectNode();
        node4.put("productName", "了不起的Node.js 正版");
        node4.put("number", 1);
        node4.put("price", 59);
        node4.put("username", "湘桂");
        arrayNode.add(node);
        arrayNode.add(node2);
        arrayNode.add(node3);
        arrayNode.add(node4);
        root.put("total", arrayNode.size());
        root.set("rows", arrayNode);

        return root;
    }

}
