package com.uavguard.utilities;

public class Item {

    public String name;
    public String version;
    public String link;
    public Status status;

    public Item() {}

    public Item(String name, String version, String link, Status status) {
        this.name = name;
        this.version = version;
        this.link = link;
    }
}
