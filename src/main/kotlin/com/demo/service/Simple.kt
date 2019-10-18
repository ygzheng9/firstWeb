package com.demo.service

import com.google.common.collect.Lists
import com.google.common.collect.Maps

import com.jfinal.kit.Kv
import com.jfinal.template.Engine
import java.util.Comparator

fun main() {
    println("Hello world!")
}

fun sum(a: Int, b: Int): Int {
    return a + b
}


fun loadTemplate(): String {
    val engine = Engine.use()

    engine.devMode = true
    engine.setToClassPathSourceFactory()

    val kv = Kv.by("name", "template")
    kv.set("type", "哈哈哈哈")

    val tmpl = engine.getTemplate("cmd/simple.html")

    // 直接输出到 String 变量

    return tmpl.renderToString(kv)
}


fun getData() : Map<String, Any> {
    val a = Lists.newArrayList<String>()
    a.add("0.first")
    a.add("2.second")
    a.add("1.第三个")

    a.sortWith(Comparator { o1, o2 -> o1.compareTo(o2, ignoreCase = true) })


    val m = Maps.newHashMap<String, Any>()
    m["items"] = a
    m["name"] = "map"
    m["content"] = "whatever you like....."

    return m;
}


fun getPoint2(grade: Int) = when {
    grade > 90 -> "GOOD"
    grade > 60 -> "OK"
    grade.hashCode() == 0x100 -> "STH"
    else -> "UN_KNOW"
}
