package com.sq.juint;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:53
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
public enum Sex {

    male("男"), female("女");
    private final String info;

    private Sex(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}

