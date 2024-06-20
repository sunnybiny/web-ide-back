package org.goorm.webide.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class WebIdeDockerConfig {

  @Bean
  public DockerClient dockerClient(){
    DefaultDockerClientConfig config = DefaultDockerClientConfig
        .createDefaultConfigBuilder()
        .withDockerHost("tcp://localhost:2375")
        .withDockerTlsVerify(false)
        .build();

    DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
        .dockerHost(config.getDockerHost())
        .build();

    return DockerClientImpl.getInstance(config, dockerHttpClient);
  }
}
