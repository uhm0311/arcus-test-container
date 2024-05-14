package com.jam2in.arcus.testcontainers;

public class ArcusContainerProps {

  private final String serviceCode;
  private final int clusterSize;
  private final int memorySize;
  private final int port;
  private final int zkPort;

  protected ArcusContainerProps(Builder builder) {
    this.serviceCode = builder.serviceCode;
    this.clusterSize = builder.clusterSize;
    this.memorySize = builder.memorySize;
    this.port = builder.port;
    this.zkPort = builder.zkPort;
  }

  public String getServiceCode() {
    return serviceCode;
  }

  public int getClusterSize() {
    return clusterSize;
  }

  public int getMemorySize() {
    return memorySize;
  }

  public int getPort() {
    return port;
  }

  public int getZkPort() {
    return zkPort;
  }

  public static class Builder {
    private String serviceCode = "test";
    private int clusterSize = 3;
    private int memorySize = 64;
    private int port = 11211;
    private int zkPort = 2181;

    public Builder serviceCode(String serviceCode) {
      this.serviceCode = serviceCode;
      return this;
    }

    public Builder clusterSize(int clusterSize) {
      this.clusterSize = clusterSize;
      return this;
    }

    public Builder memorySize(int memorySize) {
      this.memorySize = memorySize;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder zkPort(int zkPort) {
      this.zkPort = zkPort;
      return this;
    }

    public ArcusContainerProps build() {
      return new ArcusContainerProps(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
