package com.codecacher.wormhole;

enum ConnectorType {

    /**
     * 使用service进行连接，如果已经连接，则绑定服务
     */
    SERVICE_CONNECTOR_BIND(Wormhole.CONNECTOR_TYPE_SERVICE),

    /**
     * 使用service进行连接，如果已经连接，则直接返回通道
     */
    SERVICE_CONNECTOR(Wormhole.CONNECTOR_TYPE_SERVICE),

    /**
     * 使用广播进行连接，如果已经连接，则直接返回通道
     */
    BROADCAST_CONNECTOR(Wormhole.CONNECTOR_TYPE_BROADCAST);

    private int type;

    ConnectorType(int type) {
        this.type = type;
    }

    int getType() {
        return type;
    }
}
