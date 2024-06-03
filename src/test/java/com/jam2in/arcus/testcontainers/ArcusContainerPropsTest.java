package com.jam2in.arcus.testcontainers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArcusContainerPropsTest {
    
  @Test
  void testServiceCode() {
    String serviceCode = "testService";
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .serviceCode(serviceCode)
            .build();

    assertEquals(serviceCode, arcusContainerProps.getServiceCode());
  }

  @Test
  void testServiceCode_whenNull() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .serviceCode(null)
            .build());
    String expectedMessage = "Invalid service code.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testServiceCode_whenEmpty() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .serviceCode("")
            .build());
    String expectedMessage = "Invalid service code.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testServiceCode_whenNotSet() {
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
             .build();

    assertEquals("test", arcusContainerProps.getServiceCode());
   }

  @Test
  void testClusterSize() {
    int clusterSize = 5;
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .clusterSize(clusterSize)
            .build();

    assertEquals(clusterSize, arcusContainerProps.getClusterSize());
  }

  @Test
  void testClusterSize_whenSmall() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .clusterSize(0)
            .build());
    String expectedMessage = "Invalid cluster size.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testClusterSize_whenNotSet() {
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .build();

    assertEquals(3, arcusContainerProps.getClusterSize());
  }

  @Test
  void testMemorySize() {
    int memorySize = 120;
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .memorySize(memorySize)
            .build();

    assertEquals(memorySize, arcusContainerProps.getMemorySize());
  }

  @Test
  void testMemorySize_whenSmall() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .memorySize(0)
            .build());
    String expectedMessage = "Invalid memory size.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testMemorySize_whenNotSet() {
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .build();

    assertEquals(64, arcusContainerProps.getMemorySize());
  }

  @Test
  void testPort() {
    int portNum = 15244;
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .port(portNum)
            .build();

    assertEquals(portNum, arcusContainerProps.getPort());
  }

  @Test
  void testPort_whenMax() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .port(65536)
            .build());
    String expectedMessage = "Invalid port number.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testPort_whenNegative() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .port(-1)
            .build());

    String expectedMessage = "Invalid port number.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testPort_whenNotSet() {
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .build();

    assertEquals(11211, arcusContainerProps.getPort());
  }

  @Test
  void testZkPort() {
    int zkPortNum = 5789;
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .zkPort(zkPortNum)
            .build();

    assertEquals(zkPortNum, arcusContainerProps.getZkPort());
  }

  @Test
  void testZkPort_whenMax() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .zkPort(65536)
            .build());
    String expectedMessage = "Invalid zookeeper port number.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testZkPort_whenNegative() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> ArcusContainerProps.builder()
            .zkPort(-1)
            .build());
    String expectedMessage = "Invalid zookeeper port number.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testZkPort_whenNotSet() {
    ArcusContainerProps arcusContainerProps = ArcusContainerProps.builder()
            .build();

    assertEquals(2181, arcusContainerProps.getZkPort());
  }
}
