package com.cloudtest.login;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.cloudtest.configuration.SpringBoostrap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ysokolovski
 * Date: 21/11/13
 * Time: 11:56 AM
 */
@Controller
public class LoginController {




    @RequestMapping(value = "/login",params={"username","password"}, method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    LoginResult login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password) {

        if(username.equals(password)) {
            store(username,password);
            return new LoginResult(true);
        } else {
            return new LoginResult(false,"Login failed. Try again.");
        }
    }

    private static List<Login> logins=new ArrayList<Login>();

    private AmazonDynamoDBClient dynamoDB;

    public LoginController() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringBoostrap.class);
        dynamoDB = ctx.getBean(AmazonDynamoDBClient.class);
    }

    private void store(String username, String password) {
        System.out.println("Login added:"+username);

        Map<String, AttributeValue> item = newItem(username);
        PutItemRequest putItemRequest = new PutItemRequest("logins", item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
    }

    private static Map<String, AttributeValue> newItem(String username) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("username", new AttributeValue(username));
        return item;
    }


    @RequestMapping(value = "/login/",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Map<String,List<Login>> list() {
        System.out.println("List requested...");
        ScanRequest scanRequest = new ScanRequest("logins");
        ScanResult scanResult = dynamoDB.scan(scanRequest);

        List<Map<String,AttributeValue>> result= scanResult.getItems();
        List<Login> logins=new ArrayList<Login>();
        for(Map<String,AttributeValue> row:result) {
            AttributeValue value=row.get("username");
            logins.add(new Login(value.getS(),""));
        }
        Map<String,List<Login>> map=new HashMap();
        map.put("logins",logins);
        return map;
    }

}
