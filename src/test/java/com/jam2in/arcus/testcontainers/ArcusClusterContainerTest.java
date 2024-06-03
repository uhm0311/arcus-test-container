package com.jam2in.arcus.testcontainers;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.internal.OperationFuture;

import org.junit.jupiter.api.Test;

import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

class ArcusClusterContainerTest extends ArcusClusterTestBase {

  @Test
  void createArcusContainerSingle() throws ExecutionException, InterruptedException {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool("test", new ConnectionFactoryBuilder(), 2);
    
    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(ARCUS_CLUSTER_CONTAINER.isCreated()).isTrue();
    assertThat(ARCUS_CLUSTER_CONTAINER.isRunning()).isTrue();
//    assertThat(ARCUS_CLUSTER_CONTAINER.isHealthy()).isTrue();
    assertThat(set.get()).isTrue();
  }

  @Test
  void testCreateMethodWithoutParams() {
    //when
    ArcusClusterContainer container = ArcusClusterContainer.create();
    //then
    assertThat(container).isNotNull();
  }

  @Test
  void testCreateMethodWithPropsParam(){
    //given
    ArcusContainerProps props = new ArcusContainerProps.Builder().build();
    //when
    ArcusClusterContainer container = ArcusClusterContainer.create(props);
    //then
    assertThat(container).isNotNull();
  }

  @Test
  void testCreateMethodWithImageNameParam() {
    //given
    DockerImageName imageName = DockerImageName.parse("jam2in/arcus-memcached:develop");
    //when
    ArcusClusterContainer container = ArcusClusterContainer.create(imageName);
    //then
    assertThat(container).isNotNull();
  }

  @Test
  void testCreateMethodWithImageNameAndPropsParams() {
    //given
    DockerImageName imageName = DockerImageName.parse("jam2in/arcus-memcached:develop");
    ArcusContainerProps props = new ArcusContainerProps.Builder().build();
    //when
    ArcusClusterContainer container = ArcusClusterContainer.create(imageName, props);
    //then
    assertThat(container).isNotNull();
  }
}
