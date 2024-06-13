package com.jam2in.arcus.testcontainers;

/**
 * The ArcusContainerProps class represents the properties of an ArcusContainer instance.
 */
public class ArcusContainerProps {

  private final String serviceCode;
  private final int clusterSize;
  private final int memorySize;

  protected ArcusContainerProps(Builder builder) {
    this.serviceCode = builder.serviceCode;
    this.clusterSize = builder.clusterSize;
    this.memorySize = builder.memorySize;
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


  /**
   * A builder class for creating an instance of ArcusContainerProps with custom properties.
   */
  public static class Builder {
    private String serviceCode = "test";
    private int clusterSize = 3;
    private int memorySize = 64;

    /**
     * Sets the service code for configuring an instance of ArcusContainerProps.
     *
     * @param serviceCode The service code to be set. Must not be null and not empty.
     * @return The Builder object.
     * @throws IllegalArgumentException If the serviceCode is null or empty.
     */
    public Builder serviceCode(String serviceCode) {
      if (serviceCode == null || serviceCode.isEmpty()) {
        throw new IllegalArgumentException("Invalid service code.");
      }
      this.serviceCode = serviceCode;
      return this;
    }

    /**
     * Sets the cluster size for configuring an instance of ArcusContainerProps.
     *
     * @param clusterSize The cluster size to be set. Must be greater than 1.
     * @return The Builder object.
     * @throws IllegalArgumentException If the clusterSize is smaller than or equal to 1.
     */
    public Builder clusterSize(int clusterSize) {
      if (clusterSize <= 0) {
        throw new IllegalArgumentException("Invalid cluster size.");
      }
      this.clusterSize = clusterSize;
      return this;
    }

    /**
     * Sets the memory size for configuring an instance of ArcusContainerProps.
     *
     * @param memorySize The memory size to be set. Must be greater than 0.
     * @return The Builder object.
     * @throws IllegalArgumentException If the memorySize is smaller than or equal to 0.
     */
    public Builder memorySize(int memorySize) {
      if (memorySize <= 0) {
        throw new IllegalArgumentException("Invalid memory size.");
      }
      this.memorySize = memorySize;
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
