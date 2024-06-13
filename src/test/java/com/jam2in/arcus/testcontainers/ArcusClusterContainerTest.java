package com.jam2in.arcus.testcontainers;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.internal.OperationFuture;

import org.junit.jupiter.api.Test;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ArcusClusterContainerTest extends ArcusClusterTestBase {

  @Container
  private final ArcusClusterContainer clusterContainer = ArcusClusterContainer.create(
          ArcusContainerProps.builder().build());

  @Test
  void createArcusContainerSingle() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool(ARCUS_CLUSTER_CONTAINER.getHostPorts(),
            "test", new ConnectionFactoryBuilder(), 2);
    
    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(ARCUS_CLUSTER_CONTAINER.isCreated()).isTrue();
    assertThat(ARCUS_CLUSTER_CONTAINER.isRunning()).isTrue();
    assertThat(set.get()).isTrue();
  }

  @Test
  void createNotSharedClusterTest() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool(clusterContainer.getHostPorts(),
            "test", new ConnectionFactoryBuilder(), 2);

    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(clusterContainer.isCreated()).isTrue();
    assertThat(clusterContainer.isRunning()).isTrue();
    assertThat(set.get()).isTrue();
  }
}
