package com.jam2in.arcus.testcontainers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import java.io.IOException;
import java.util.ArrayList;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

/**
 * The ArcusClusterContainer class represents a containerized Arcus cluster.
 * It extends the GenericContainer class and provides methods for starting, stopping, and managing the cluster.
 *
 * This class requires a ZookeeperContainer class, which is also a containerized component used by ArcusClusterContainer.
 * The ZookeeperContainer class is responsible for managing the ZooKeeper instance used by the Arcus cluster.
 *
 * Note: To use this class, you should have Docker installed on your machine.
 */
public class ArcusClusterContainer extends GenericContainer<ArcusClusterContainer> {

  private static final int DEFAULT_ZK_CONTAINER_PORT = 2181;

  private static final String CREATE_CMD = "create";
  private static final String ZK_CLI_PATH = "./bin/zkCli.sh";
  private static final String ZPATH_ARCUS = "/arcus";
  private static final String ZPATH_CACHE_LIST = ZPATH_ARCUS + "/cache_list";
  private static final String ZPATH_CLIENT_LIST = ZPATH_ARCUS + "/client_list";
  private static final String ZPATH_CACHE_SERVER_MAPPING = ZPATH_ARCUS + "/cache_server_mapping";

  private final Network network;
  private final ZookeeperContainer zkContainer;
  private final String serviceCode;

  private final ArrayList<String> cacheNodes = new ArrayList<>();
  private final ArrayList<ArcusContainer> containers = new ArrayList<>();

  private ArcusClusterContainer(DockerImageName imageName, ArcusContainerProps props) {
    this.network = Network.newNetwork();
    this.zkContainer = new ZookeeperContainer(network, props.getZkPort());
    this.serviceCode = props.getServiceCode();

    for (int i = 0; i < props.getClusterSize(); i++) {
      String address = "cache" + (i + 1) + ":" + (props.getPort() + i);
      cacheNodes.add(address);
      containers.add(new ArcusContainer(imageName, address, network, props.getMemorySize()));
    }
  }

  public static ArcusClusterContainer create() {
    return new ArcusClusterContainer(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME, new ArcusContainerProps.Builder().build());
  }

  public static ArcusClusterContainer create(ArcusContainerProps props) {
    return new ArcusClusterContainer(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME, props);
  }

  public static ArcusClusterContainer create(DockerImageName imageName) {
    return new ArcusClusterContainer(imageName, new ArcusContainerProps.Builder().build());
  }

  public static ArcusClusterContainer create(DockerImageName imageName, ArcusContainerProps props) {
    return new ArcusClusterContainer(imageName, props);
  }

  @Override
  public void start() {
    zkContainer.start();
    try {
      createZnode();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    containers.stream().forEach(ac -> ac.start());
  }

  @Override
  public boolean isRunning() {
    return containers.stream().allMatch(c -> c.isRunning()) && zkContainer.isRunning();
  }

  @Override
  public void stop() {
    containers.stream().forEach(ac -> ac.stop());
    zkContainer.stop();
  }

  @Override
  public boolean isCreated() {
    return containers.stream().allMatch(c -> c.isCreated()) && zkContainer.isCreated();
  }

  @Override
  public boolean isHealthy() {
    return containers.stream().allMatch(c -> c.isHealthy()) && zkContainer.isHealthy();
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

    public ZookeeperContainer(Network network, int port) {
      super(DEFAULT_ZK_IMAGE_NAME);
      withNetwork(network);
      withEnv("ZOO_MY_ID", "1");
      withCreateContainerCmdModifier(cmd -> {
        cmd.withHostName("zoo1");
        cmd.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(DEFAULT_ZK_CONTAINER_PORT)));
      });
      withExposedPorts(DEFAULT_ZK_CONTAINER_PORT);
    }
  }
}
