package com.ljh2017.footprint;

/**
 * Created by alfo06-15 on 2017-08-11.
 */

public class Memo {

    String name;
    String imgpic;
    String memo;
    String addr;
    String tag;
    //boolean isLock;

    public Memo(String name, String imgpic, String memo, String addr, String tag) {
        this.name = name;
        this.imgpic = imgpic;
        this.memo = memo;
        this.addr = addr;
        this.tag = tag;
        //this.isLock = isLock;
    }

    public Memo(String imgpic) {
        this.imgpic = imgpic;
    }
}

