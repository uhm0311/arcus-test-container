package com.jam2in.arcus.testcontainers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.RestartPolicy;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class ArcusContainer extends GenericContainer<ArcusContainer> {

  public static final DockerImageName DEFAULT_ARCUS_IMAGE_NAME = DockerImageName.parse("jam2in/arcus-memcached");

  private ArcusContainer(DockerImageName dockerImageName, ArcusContainerProps props) {
    super(dockerImageName);
    setupContainer(props.getPort(), props.getMemorySize());
  }

  ArcusContainer(DockerImageName imageName, String address, Network network, int memSize) {
    super(imageName);
    String[] split = address.split(":");
    String host = split[0];
    int port = Integer.parseInt(split[1]);
    withNetwork(network);
    setupContainer(port, host, network, memSize);
  }

  public static ArcusContainer create() {
    return new ArcusContainer(DEFAULT_ARCUS_IMAGE_NAME, new ArcusContainerProps.Builder().build());
  }

  public static ArcusContainer create(ArcusContainerProps props) {
    return new ArcusContainer(DEFAULT_ARCUS_IMAGE_NAME, props);
  }

  public static ArcusContainer create(DockerImageName imageName) {
    return new ArcusContainer(imageName, new ArcusContainerProps.Builder().build());
  }

  private void setupContainer(int port, int memSize) {
    setupContainer(port, null, null, memSize);
  }

  private void setupContainer(int port, String host, Network network, int memorySize) {
    withCreateContainerCmdModifier(cmd -> {
      if (host != null) {
        cmd.withHostName(host);
      }
      cmd.getHostConfig().withRestartPolicy(RestartPolicy.alwaysRestart());
      cmd.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port)));
    });
    withExposedPorts(port);
    withCommand(buildContainerCommand(port, network, memorySize));
  }

  private String buildContainerCommand(int port, Network network, int memorySize) {
    StringBuilder sb = new StringBuilder();
    sb.append("-m " + memorySize + " ");
    sb.append("-p " + port);

    if (network != null) {
      sb.append(" -z zoo1:2181");
      withEnv("ARCUS_CACHE_PUBLIC_IP", "127.0.0.1");
    }
    return sb.toString();
  }
}
