package com.jam2in.arcus.testcontainers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.RestartPolicy;

import java.util.Objects;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

/**
 * <p>ArcusContainer represents a Docker container running Arcus Memcached.</p>
 *
 * <p>ArcusContainer extends GenericContainer, which is used for creating and managing Docker containers.</p>
 *
 * <p>Usage:</p>
 *
 * <pre>{@code
 * // Create a new instance of ArcusContainer with default image name and properties
 * ArcusContainer container = ArcusContainer.create();
 *
 * // Create a new instance of ArcusContainer with custom properties
 * ArcusContainerProps props = new ArcusContainerProps.Builder()
 *                                 .memorySize(128)
 *                                 .port(11221)
 *                                 .build();
 * ArcusContainer container = ArcusContainer.create(props);
 *
 * // Create a new instance of ArcusContainer with custom Docker image name
 * DockerImageName imageName = DockerImageName.parse("jam2in/arcus-memcached:latest");
 * ArcusContainer container = ArcusContainer.create(imageName);
 * }</pre>
 *
 * Note: To use this class, you should have Docker installed on your machine.
 */
public class ArcusContainer extends GenericContainer<ArcusContainer> implements PortAllocator {

  public static final DockerImageName DEFAULT_ARCUS_IMAGE_NAME = DockerImageName.parse("jam2in/arcus-memcached");

  private ArcusContainer(DockerImageName dockerImageName, ArcusContainerProps props) {
    super(dockerImageName);
    setupContainer(getPort(), props.getMemorySize());
  }

  ArcusContainer(DockerImageName imageName, String address, Network network, int memSize) {
    super(imageName);
    String[] split = address.split(":");
    String host = split[0];
    int port = Integer.parseInt(split[1]);
    withNetwork(network);
    setupContainer(port, host, network, memSize);
  }

  /**
   * Creates a new instance of ArcusContainer with default image name and properties.
   *
   * @return a new instance of {@link ArcusContainer} instance
   *
   * @see ArcusContainer#DEFAULT_ARCUS_IMAGE_NAME
   * @see ArcusContainerProps.Builder#build()
   */
  public static ArcusContainer create() {
    return new ArcusContainer(DEFAULT_ARCUS_IMAGE_NAME, new ArcusContainerProps.Builder().build());
  }

  /**
   * Creates a new instance of ArcusContainer with the given properties.
   *
   * @param props The properties to configure the ArcusContainer.
   * @return a new instance of {@link ArcusContainer} instance
   */
  public static ArcusContainer create(ArcusContainerProps props) {
    return new ArcusContainer(DEFAULT_ARCUS_IMAGE_NAME, props);
  }

  /**
   * Creates a new instance of ArcusContainer with the given image name.
   *
   * @param imageName The Docker image name.
   * @return a new instance of {@link ArcusContainer} instance
   */
  public static ArcusContainer create(DockerImageName imageName) {
    return new ArcusContainer(imageName, new ArcusContainerProps.Builder().build());
  }

  /**
   * Creates a new instance of ArcusContainer with the given image name and props.
   *
   * @param imageName The Docker image name.
   * @param props The properties to configure the ArcusContainer.
   * @return a new instance of {@link ArcusContainer} instance
   */
  public static ArcusContainer create(DockerImageName imageName, ArcusContainerProps props) {
    return new ArcusContainer(imageName, props);
  }

  private void setupContainer(int port, int memSize) {
    setupContainer(port, null, null, memSize);
  }

  private void setupContainer(int port, String host, Network network, int memorySize) {
    this.withCreateContainerCmdModifier(cmd -> {
      if (host != null) {
        cmd.withHostName(host);
      }
      Objects.requireNonNull(cmd.getHostConfig())
              .withRestartPolicy(RestartPolicy.alwaysRestart())
              .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port)));
    });
    this.withExposedPorts(port);
    this.withCommand(buildContainerCommand(port, network, memorySize));
  }

  private String buildContainerCommand(int port, Network network, int memorySize) {
    StringBuilder sb = new StringBuilder();
    sb.append("-m ").append(memorySize).append(" ");
    sb.append("-p ").append(port);

    if (network != null) {
      sb.append(" -z zoo1:2181");
      this.withEnv("ARCUS_CACHE_PUBLIC_IP", "127.0.0.1");
    }
    return sb.toString();
  }
}
