package com.jam2in.arcus.testcontainers;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.internal.OperationFuture;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is a test class for the ArcusClusterContainer class, extending the ArcusClusterTestBase class.
 * Test with singleton ArcusContainer object.
 */
class ArcusClusterContainerTestOther extends ArcusClusterTestBase {

  @Test
  void createArcusContainerSingle() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool(ARCUS_CLUSTER_CONTAINER.getHostPorts(),
            "test", new ConnectionFactoryBuilder(), 2);
    
    //when
    OperationFuture<Boolean> set = arcusClient.set("test2", 10, "testValue2");

    //then
    assertThat(ARCUS_CLUSTER_CONTAINER.isRunning()).isTrue();
    assertThat(set.get()).isTrue();
  }
}
