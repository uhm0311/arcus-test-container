package com.jam2in.arcus.testcontainers;

import java.io.IOException;
import java.util.ArrayList;

import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

/**
 * The ArcusClusterContainer class represents a containerized Arcus cluster.
 * It extends the GenericContainer class and provides methods for starting, stopping, and managing the cluster.
 *
 * <p>
 * This class requires a ZookeeperContainer class, which is also a containerized component used by ArcusClusterContainer.
 * The ZookeeperContainer class is responsible for managing the ZooKeeper instance used by the Arcus cluster.
 * </p>
 *
 * <p>
 * The cluster consists of multiple Arcus containers and a Zookeeper container.
 * Each Arcus container is associated with a cache node and a client node.
 * The Zookeeper container is responsible for managing the cluster metadata.
 * </p>
 *
 * <p>
 * To create an instance of ArcusClusterContainer, you can use the static factory methods provided.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * ArcusClusterContainer container = ArcusClusterContainer.create();
 * container.start();
 * }</pre>
 *
 * <p>
 * After starting the container, you can use methods like {@link #isRunning()} and {@link #isHealthy()} to check the state of the container.
 * </p>
 *
 * <p>
 * The ArcusClusterContainer class also defines a private inner class ZookeeperContainer,
 * which represents a Zookeeper container that extends the GenericContainer class.
 * </p>
 *
 * @see GenericContainer
 *
 * Note: To use this class, you should have Docker installed on your machine.
 */
public class ArcusClusterContainer extends GenericContainer<ArcusClusterContainer> implements PortAllocator {

  private static final int DEFAULT_ZK_CONTAINER_PORT = 2181;

  private static final String CREATE_CMD = "create";
  private static final String ZK_CLI_PATH = "./bin/zkCli.sh";
  private static final String ZPATH_ARCUS = "/arcus";
  private static final String ZPATH_CACHE_LIST = ZPATH_ARCUS + "/cache_list";
  private static final String ZPATH_CLIENT_LIST = ZPATH_ARCUS + "/client_list";
  private static final String ZPATH_CACHE_SERVER_MAPPING = ZPATH_ARCUS + "/cache_server_mapping";

  private final ZookeeperContainer zkContainer;
  private final String serviceCode;

  private final ArrayList<String> cacheNodes = new ArrayList<>();
  private final ArrayList<ArcusContainer> containers = new ArrayList<>();

  private ArcusClusterContainer(DockerImageName imageName, ArcusContainerProps props) {
    super(imageName);

    final Network network = Network.newNetwork();
    this.zkContainer = new ZookeeperContainer(network);
    this.serviceCode = props.getServiceCode();

    for (int i = 0; i < props.getClusterSize(); i++) {
      String address = "cache" + (i + 1) + ":" + getPort();
      cacheNodes.add(address);
      containers.add(new ArcusContainer(imageName, address, network, props.getMemorySize()));
    }
  }

  /**
   * Creates a new instance of ArcusClusterContainer with default image name and properties.
   *
   * @return a new instance of {@link ArcusClusterContainer}
   */
  public static ArcusClusterContainer create() {
    return new ArcusClusterContainer(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME, new ArcusContainerProps.Builder().build());
  }

  /**
   * Create a new instance of ArcusClusterContainer with the given ArcusContainerProps.
   *
   * @param props the ArcusContainerProps for configuring the ArcusClusterContainer
   * @return a new instance of {@link ArcusClusterContainer}
   */
  public static ArcusClusterContainer create(ArcusContainerProps props) {
    return new ArcusClusterContainer(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME, props);
  }

  /**
   * Creates a new instance of ArcusClusterContainer with the given DockerImageName.
   *
   * @param imageName the DockerImageName for the container
   * @return a new instance of {@link ArcusClusterContainer}
   */
  public static ArcusClusterContainer create(DockerImageName imageName) {
    return new ArcusClusterContainer(imageName, new ArcusContainerProps.Builder().build());
  }

  /**
   * Creates a new instance of {@link ArcusClusterContainer} with the given {@link DockerImageName}
   * and {@link ArcusContainerProps}.
   *
   * @param imageName the {@link DockerImageName} for the container
   * @param props     the {@link ArcusContainerProps} for configuring the {@link ArcusClusterContainer}
   * @return a new instance of {@link ArcusClusterContainer}
   */
  public static ArcusClusterContainer create(DockerImageName imageName, ArcusContainerProps props) {
    return new ArcusClusterContainer(imageName, props);
  }

  /**
   * invoke for creating ArcusClientPool.
   * @return a cluster zk host posts address.
   */
  public String getHostPorts() {
    return "127.0.0.1" + ":" + getFirstMappedPort();
  }

  @Override
  public Integer getFirstMappedPort() {
    return zkContainer.getFirstMappedPort();
  }

  @Override
  public void start() {
    zkContainer.start();
    try {
      createZnode();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    containers.forEach(ArcusContainer::start);
  }

  @Override
  public boolean isRunning() {
    return containers.stream().allMatch(ContainerState::isRunning) && zkContainer.isRunning();
  }

  @Override
  public void stop() {
    containers.forEach(ArcusContainer::stop);
    zkContainer.stop();
  }

  @Override
  public boolean isCreated() {
    return containers.stream().allMatch(ContainerState::isCreated) && zkContainer.isCreated();
  }

  @Override
  public boolean isHealthy() {
    return containers.stream().allMatch(ContainerState::isHealthy) && zkContainer.isHealthy();
  }

  private void createZnode() throws IOException, InterruptedException {
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_ARCUS);
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CACHE_LIST);
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CACHE_LIST + "/" + serviceCode);
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CLIENT_LIST);
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CLIENT_LIST + "/" + serviceCode);
    zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CACHE_SERVER_MAPPING);

    for (String nodeAddr : cacheNodes) {
      zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CACHE_SERVER_MAPPING + "/" + nodeAddr);
      zkContainer.execInContainer(ZK_CLI_PATH, CREATE_CMD, ZPATH_CACHE_SERVER_MAPPING + "/" + nodeAddr + "/" + serviceCode);
    }
  }

  /**
   * This class represents a Zookeeper container that extends the GenericContainer class.
   */
  private static class ZookeeperContainer extends GenericContainer<ZookeeperContainer> {
    private static final DockerImageName DEFAULT_ZK_IMAGE_NAME = DockerImageName.parse("zookeeper:3.5.9");

    public ZookeeperContainer(Network network) {
      super(DEFAULT_ZK_IMAGE_NAME);

      this.withNetwork(network);
      this.withEnv("ZOO_MY_ID", "1");
      this.withCreateContainerCmdModifier(cmd -> {
        cmd.withHostName("zoo1");
      });
      this.withExposedPorts(DEFAULT_ZK_CONTAINER_PORT);
    }
  }
}
