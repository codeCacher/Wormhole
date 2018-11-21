package com.codecacher.wormhole;

public enum ConnectorType {

    SERVICE_CONNECTOR(Wormhole.CONNECTOR_TYPE_SERVICE),
    BROADCAST_CONNECTOR(Wormhole.CONNECTOR_TYPE_BROADCAST);

    private int type;

    ConnectorType(int type) {
        this.type = type;
    }

    int getType() {
        return type;
    }
}
