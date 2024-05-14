package com.jam2in.arcus.testcontainers;

/**
 * The ArcusClusterTestBase class is a base class for testing Arcus clusters.
 * It provides a static instance of ArcusClusterContainer that can be used to start
 * and manage Arcus cluster instances for testing purposes.
 */
public class ArcusClusterTestBase {

  static final ArcusClusterContainer ARCUS_CLUSTER_CONTAINER;

  static {
    ARCUS_CLUSTER_CONTAINER = ArcusClusterContainer.create();
    ARCUS_CLUSTER_CONTAINER.start();
  }
}
