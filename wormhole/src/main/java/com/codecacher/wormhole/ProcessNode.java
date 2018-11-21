package com.codecacher.wormhole;

public class ProcessNode implements INode {
    private String name;

    public ProcessNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
