package com.georgebanin.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "ping")
public interface DbProps {
    String host();
    int port();
    String db();
    String username();
    String password();


}
