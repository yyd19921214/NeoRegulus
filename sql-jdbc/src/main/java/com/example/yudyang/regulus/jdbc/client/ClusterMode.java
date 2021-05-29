package com.example.yudyang.regulus.jdbc.client;

public enum ClusterMode {
    /**
     * 集群模式
     */
    CLUSTER("cluster"),
    /**
     * 单节点模式
     */
    SINGLE("single");

    private String name;

    ClusterMode(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public static ClusterMode getByValue(String mode){
        for (ClusterMode clusterMode:values()){
            if (clusterMode.getName().equalsIgnoreCase(mode)){
                return clusterMode;
            }
        }
        throw new IllegalArgumentException("unsupported cluster mode " + mode);
    }
}
