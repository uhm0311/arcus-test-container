package com.jam2in.arcus.testcontainers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.DefaultConnectionFactory;

import org.junit.jupiter.api.Test;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ArcusContainerTest {

  @Container
  private final ArcusContainer arcusContainer = ArcusContainer.create(
          ArcusContainerProps.builder()
                  .port(11214)
                  .build());


  @Test
  void createArcusContainerSingle() throws IOException, ExecutionException, InterruptedException {
    //given
    ArrayList<InetSocketAddress> list = new ArrayList<>();
    list.add(new InetSocketAddress("127.0.0.1", 11214));

    //when
    ArcusClient arcusClient = new ArcusClient(new DefaultConnectionFactory(), list);
    Boolean b = arcusClient.set("test", 10, "singleTestValue").get();

    //then
    assertThat(arcusContainer.isRunning()).isTrue();
    assertThat(b).isTrue();
  }
}
