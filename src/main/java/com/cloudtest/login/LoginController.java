package com.cloudtest.login;


import com.amazonaws.AmazonServiceException;
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

            if(username.equals("start")){
                doTest();
            }
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
        System.out.println("Login added:"+username + "/" + password);

        Map<String, AttributeValue> item = newItem(username, password);
        PutItemRequest putItemRequest = new PutItemRequest("logins", item);  // table name, object
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
    }

    private static Map<String, AttributeValue> newItem(String username, String password) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("username", new AttributeValue(username));
        item.put("password", new AttributeValue(password));
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
            AttributeValue value=row.get("username");  //col/hashkey name
            AttributeValue value2=row.get("password");
            logins.add(new Login(value.getS(),value2!=null? value2.getS():""));
        }
        Map<String,List<Login>> map=new HashMap<String,List<Login>>();
        map.put("logins",logins);
        return map;
    }


    final static String TestTableName = "itemsTable";
    final static String TestKeyName = "extId";

    public void doTest(){
        System.out.println("starting test");

        long startTime = System.currentTimeMillis();

        deleteTestTable();
        boolean b = waitForTableDeletion(TestTableName);
        if(!b){
            System.out.println("Table could not be not deleted.");
            return;
        }
        System.out.println("Table deleted in " + (System.currentTimeMillis() - startTime) +" ms." );

        startTime = System.currentTimeMillis();
        createTestTable();
        b = waitForTableCreation(TestTableName);
        if(!b){
            System.out.println("Table could not be not created.");
            return;
        }
        System.out.println("Table created in " + (System.currentTimeMillis() - startTime) +" ms." );

        startTime = System.currentTimeMillis();
        createTestItems();
        System.out.println("Test items created in " + (System.currentTimeMillis() - startTime) +" ms." );

        startTime = System.currentTimeMillis();
        storeTestItems();
        System.out.println("Test items stored in " + (System.currentTimeMillis() - startTime) +" ms." );

        startTime = System.currentTimeMillis();
        scanTestItemTable(TestTableName);
        System.out.println("Test items scanned in " + (System.currentTimeMillis() - startTime) +" ms." );
    }

    private void deleteTestTable(){
        try {
            DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(TestTableName);
            DeleteTableResult result = dynamoDB.deleteTable(deleteTableRequest);
            waitForTableDeletion(TestTableName);
        } catch (ResourceNotFoundException ignore) {
            //doesn't even exist

        }
    }

    private boolean waitForTableDeletion(String tableName) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + 120000L) {
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                DescribeTableResult tableDescription = dynamoDB.describeTable(tableName);
                if (tableDescription.getTable().getTableStatus().equals(TableStatus.ACTIVE.toString()))
                    return false;
                Thread.sleep(1000);
            } catch (ResourceNotFoundException e) {
                System.out.println("Table " + tableName + " is not found. It was deleted.");
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Table " + tableName + " was never deleted");
    }

    private void createTestTable() {
        try {
            System.out.println("Creating table " + TestTableName +" / " + "itemId");
            ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

            ks.add(new KeySchemaElement().withAttributeName(TestKeyName).withKeyType(KeyType.HASH));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName(TestKeyName).withAttributeType("S"));

            ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput()
                    .withReadCapacityUnits(1L)
                    .withWriteCapacityUnits(1L);

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(TestTableName)
                    .withKeySchema(ks)
                    .withProvisionedThroughput(provisionedthroughput);
            request.setAttributeDefinitions(attributeDefinitions);
            CreateTableResult result = dynamoDB.createTable(request);

        } catch (AmazonServiceException ase) {
            System.err.println("Failed to create table " + TestTableName + " " + ase);
        }
    }

    private boolean waitForTableCreation(String tableName) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + 120000L) {
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                DescribeTableResult tableDescription = dynamoDB.describeTable(tableName);
                if (tableDescription.getTable().getTableStatus().equals(TableStatus.ACTIVE.toString()))
                    return true;
                Thread.sleep(1000);
            } catch (ResourceNotFoundException e) {
                System.out.println("Table " + tableName + " is not found. It was deleted.");
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Table " + tableName + " was never deleted");
    }


    int MAX_ITEMS = 1000;

    List<Floc> testItems = new ArrayList<Floc>();
    private void createTestItems(){
        for (int i = 0; i < MAX_ITEMS; i++) {
            String key = UUID.randomUUID().toString();
            Floc floc = new Floc(key, "name"+key, "parent"+key,null);
            testItems.add(floc);
        }
    }

    private void storeTestItems(){
        int i = 0;
        for (Floc o : testItems) {
            storeTestItem(o.extId, o.name, o.parentId);
            i++;
            if( i%100 == 0){
                System.out.println("Stored " + i + " items");
            }
        }

    }

    private void storeTestItem(String id, String name, String parentId) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(TestKeyName, new AttributeValue(id));
        item.put("name", new AttributeValue(name));
        item.put("parentId", new AttributeValue(parentId));


        PutItemRequest putItemRequest = new PutItemRequest(TestTableName, item);  // table name, object
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
    }

    private void scanTestItemTable(String testTableName) {
        ScanRequest scanRequest = new ScanRequest(testTableName);
        ScanResult scanResult = dynamoDB.scan(scanRequest);

        List<Map<String,AttributeValue>> result= scanResult.getItems();
        System.out.println(" result count = " + result.size());
    }

}
