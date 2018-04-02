package com.example.leejinseong.nomadhackathone.model

import io.realm.RealmObject
import io.realm.annotations.Required

/**
 * Created by leejinseong on 2018. 3. 18..
 */

open class Money : RealmObject() {

    @Required
    var money: Int? = null

    @Required
    var date1: String? = null //yyyy/MM/dd

    @Required
    var date2: String? = null //yyyy/MM/dd/HH/mm/ss
}



