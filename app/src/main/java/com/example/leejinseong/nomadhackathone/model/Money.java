package com.example.leejinseong.nomadhackathone.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by leejinseong on 2018. 3. 18..
 */

public class Money extends RealmObject {

    @Required
    private String money;

    @Required
    private String date1; //yyyy/MM/dd

    @Required
    private String date2; //yyyy/MM/dd/HH/mm/ss

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }
}



