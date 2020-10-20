package com.zhm.zookeeper.zk;

import java.util.List;

public interface Loadbalace {

    String selectHost(List<String> repos);
}
