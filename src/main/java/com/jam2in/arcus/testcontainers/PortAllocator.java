package com.jam2in.arcus.testcontainers;

import java.io.IOException;
import java.net.ServerSocket;

public interface PortAllocator {

  default int getPort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException("memcached node port exception in PortAllocator.", e);
    }
  }
}
