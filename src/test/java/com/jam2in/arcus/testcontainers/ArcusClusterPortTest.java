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
public class ArcusClusterPortTest {

  @Container
  private ArcusClusterContainer arcusClusterContainer = ArcusClusterContainer
          .create(ArcusContainerProps.builder()
          .zkPort(2191)
          .port(21211)
          .build());

  @Test
  void customZkPortTest() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient
            .createArcusClientPool("127.0.0.1:2191", "test", new ConnectionFactoryBuilder(), 3);

    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(arcusClusterContainer.isRunning()).isTrue();
    assertThat(set.get()).isTrue();
  }
}
