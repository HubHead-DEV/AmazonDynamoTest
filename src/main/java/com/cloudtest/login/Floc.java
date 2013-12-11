package com.cloudtest.login;

/**
 * Created by ekar on 12/11/13.
 */
public class Floc {
    //simple floc

    String extId;
    String name;
    String parentId;
    String[] classIds;

    public Floc(String extId, String name, String parentId, String[] classIds){
        this.extId = extId;
        this.name = name;
        this.parentId = parentId;
        this.classIds = classIds;
    }

//    public String toJSon(){
//
//    }

}
