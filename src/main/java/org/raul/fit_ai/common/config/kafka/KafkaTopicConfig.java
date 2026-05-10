package org.raul.fit_ai.common.config.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Duration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaTopicConfig {

	// Critical
	@Value("${app.kafka.topics.critical.name}")
	private String criticalTopicName;

	@Value("${app.kafka.topics.critical.partitions}")
	private int criticalPartitions;

	@Value("${app.kafka.topics.critical.replicas}")
	private short criticalReplicas;

	// Transactional
	@Value("${app.kafka.topics.transactional.name}")
	private String transactionalTopicName;

	@Value("${app.kafka.topics.transactional.partitions}")
	private int transactionalPartitions;

	@Value("${app.kafka.topics.transactional.replicas}")
	private short transactionalReplicas;

	// Scheduled
	@Value("${app.kafka.topics.scheduled.name}")
	private String scheduledTopicName;

	@Value("${app.kafka.topics.scheduled.partitions}")
	private int scheduledPartitions;

	@Value("${app.kafka.topics.scheduled.replicas}")
	private short scheduledReplicas;

	@Bean
	public NewTopic notificationsCriticalTopic() {
		return TopicBuilder.name(criticalTopicName)
				.partitions(criticalPartitions)
				.replicas(criticalReplicas)
				.config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofDays(7).toMillis()))
				.build();
	}

	@Bean
	public NewTopic notificationsTransactionalTopic() {
		return TopicBuilder.name(transactionalTopicName)
				.partitions(transactionalPartitions)
				.replicas(transactionalReplicas)
				.config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofDays(3).toMillis()))
				.build();
	}

	@Bean
	public NewTopic notificationsScheduledTopic() {
		return TopicBuilder.name(scheduledTopicName)
				.partitions(scheduledPartitions)
				.replicas(scheduledReplicas)
				.config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofDays(1).toMillis()))
				.build();
	}
}
