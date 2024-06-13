package com.jam2in.arcus.testcontainers;

import org.junit.jupiter.api.Test;

import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.assertj.core.api.Assertions.assertThat;

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
  void testCreateMethodWithoutParams() {
    //when
    ArcusClusterContainer container = ArcusClusterContainer.create();
    //then
    assertThat(container).isNotNull();
    assertThat(container.getDockerImageName())
            .isEqualTo(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME.toString());
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
    assertThat(container.getDockerImageName()).isEqualTo(imageName.toString());
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

  @Test
  void createArcusContainerWithPropsTest() {
    //given
    ArcusContainerProps props = ArcusContainerProps.builder()
            .memorySize(512)
            .build();

    //when
    ArcusContainer arcusContainer = ArcusContainer.create(props);

    //then
    assertThat(arcusContainer.getDockerImageName())
            .isEqualTo(ArcusContainer.DEFAULT_ARCUS_IMAGE_NAME.toString());
  }
}
