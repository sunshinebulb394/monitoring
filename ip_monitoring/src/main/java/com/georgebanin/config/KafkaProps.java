package com.georgebanin.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "kafka")
public interface KafkaProps {
    String bootstrapServers();
    String keySerializer();
    String valueSerializer();
    String acks();
    String enableAutoCommit();

    String groupId();
    String autoOffsetReset();

}
