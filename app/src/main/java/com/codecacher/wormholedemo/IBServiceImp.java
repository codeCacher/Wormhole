package com.codecacher.wormholedemo;

public class IBServiceImp extends IBService.Stub {
    @Override
    public String getName(int id) {
        return "" + id;
    }
}
