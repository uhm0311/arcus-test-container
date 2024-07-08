# Arcus TestContainer

![CI](https://github.com/jam2in/arcus-test-container/actions/workflows/ci.yml/badge.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

Testcontainers Arcus is an extension of Testcontainers that supports Arcus (Standalone and Cluster) containers.
___
## Getting Started
___
### Gradle

#### Repository

```groovy
repositories {
    url "https://oss.sonatype.org/content/repositories/snapshots"   
}
```

#### Dependency

```groovy
dependencies {
    testImplementation "com.jam2in.arcus:arcus-test-container:0.0.1-SNAPSHOT"
}
```

### Maven

#### Repository

```xml
<repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases><enabled>false</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
</repository>
```

#### Dependency

```xml
<dependency>
    <groupId>com.jam2in.arcus</groupId>
    <artifactId>arcus-test-container</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Usage
### container that are restarted for every test method
1. single node
```java
@Testcontainers
public class ArcusContainerTest {

  @Container
  private final ArcusContainer arcusContainer = ArcusContainer.create();
  
  @Test
  void testcase() {
    //given
    ArcusClient arcusClient = new ArcusClient(
            new DefaultConnectionFactory(),
            new ArrayList<>(Arrays.asList(new InetSocketAddress(
                    "127.0.0.1", 
                    arcusContainer.getFirstMappedPort()))));
    
    //when
    Boolean b = arcusClient.set("test", 10, "singleTestValue").get();
    
    //then
    assertThat(b).isTrue();
  }
}
```
2. cluster

```java
@Testcontainers
public class ArcusClusterContainerTest {

  @Container
  private final ArcusClusterContainer arcusCluster = ArcusClusterContainer.create();
  
  @Test
  void testcase() {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool(
            arcusCluster.getHostPorts(), 
            "test", 
            new ConnectionFactoryBuilder(),
            2);
    
    //when
    Boolean b = arcusClient.set("test", 10, "singleTestValue").get();

    //then
    assertThat(b).isTrue();
  }
}
```
### container that are shared between all methods of a test class
1. single node

```java
public class ArcusContainerTestBase {

  protected static final ArcusContainer ARCUS_CONTAINER;

  static {
    ARCUS_CONTAINER = ArcusContainer.create();
    ARCUS_CONTAINER.start();
  }
}
```
```java
public class ArcusContainerTest extends ArcusContainerTestBase {

  @Test
  void testcase() {
    //given
    ArcusClient arcusClient = new ArcusClient(
            new DefaultConnectionFactory(),
            new ArrayList<>(Arrays.asList(new InetSocketAddress(
                    "127.0.0.1", 
                    ARCUS_CONTAINER.getFirstMappedPort()))));
    
    //when
    Boolean b = arcusClient.set("test", 10, "singleTestValue").get();
    
    //then
    assertThat(b).isTrue();
  }
}
```

2. cluster

```java
public class ArcusClusterTestBase {

  protected static final ArcusClusterContainer ARCUS_CLUSTER_CONTAINER;

  static {
    ARCUS_CLUSTER_CONTAINER = ArcusClusterContainer.create();
    ARCUS_CLUSTER_CONTAINER.start();
  }
}
```
```java
public class ArcusClusterContainerTest extends ArcusClusterTestBase {

  @Test
  void createArcusContainerSingle() {
    //given
    ArcusClientPool arcusClient = ArcusClient.createArcusClientPool(
            ARCUS_CLUSTER_CONTAINER.getHostPorts(), 
            "test", 
            new ConnectionFactoryBuilder(), 
            2);

    //when
    OperationFuture<Boolean> set = arcusClient.set("test", 10, "testValue");

    //then
    assertThat(set.get()).isTrue();
  }
}
```