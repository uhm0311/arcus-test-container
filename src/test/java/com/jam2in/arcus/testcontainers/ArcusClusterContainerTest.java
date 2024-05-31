package com.jam2in.arcus.testcontainers;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.internal.OperationFuture;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArcusClusterContainerTest extends ArcusClusterTestBase {

  @Test
  void createArcusContainerSingle() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool("test", new ConnectionFactoryBuilder(), 2);
    
    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(ARCUS_CLUSTER_CONTAINER.isRunning()).isTrue();
    assertThat(set.get()).isTrue();
  }
}
