package com.springboot.data.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private String id;
    private String attr1;
    private String attr2;

    public Data(
            String id,
            String attr1,
            String attr2
    ) {
        this.id=id;
        this.attr1=attr1;
        this.attr2=attr2;
    }
    public Data(Data data) {
        this.id=data.getId();
        this.attr1=data.getAttr1();
        this.attr2=data.getAttr2();
    }
    public String getId() {
        return id;
    }
    public String getAttr1() {
        return attr1;
    }
    public String getAttr2() {
        return attr2;
    }

    public void setId(String id) {
        this.id=id;
    }
    public void setAttr1(String attr1) {
        this.attr1=attr1;
    }
    public void setAttr2(String attr2) {
        this.attr2=attr2;
    }

    @Override
    public String toString() {
        return
                "Data["
                        + "id=" + id +", "
                        + "attr1=" + attr1 +", "
                        + "attr2=" + attr2 +", "
                        + "]";
    }

    public static void main(String[] args) {
        int a = 12000;
        int b = 300000;
        System.out.println(a/b);
    }
}