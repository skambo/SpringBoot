package io.skambo.example.infrastructure.messaging.config

import io.skambo.example.application.domain.model.User
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@EnableKafka
@Configuration
class KafkaConfig {
    @Value("\${kafka.bootstrap-address}")
    private val bootstrapAddress: String? = null

    @Bean
    fun producerFactory(): ProducerFactory<String, User> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        // What type of key 
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        // Now this is most important, this tells kafka to use default Json format 
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory<String, User>(config)
    }

    @Bean
    fun kafkaTemplate():KafkaTemplate<String, User>{
        return KafkaTemplate<String, User>(producerFactory())
    }

    @Bean
    fun consumerFactory():ConsumerFactory<String, String>{
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        config[ConsumerConfig.GROUP_ID_CONFIG] = "group_id"
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

        return DefaultKafkaConsumerFactory<String, String>(config)
    }

    @Bean
    fun kafkaListenerContainerFactory():ConcurrentKafkaListenerContainerFactory<String, String>{
        val factory:ConcurrentKafkaListenerContainerFactory<String, String> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun userConsumerFactory():ConsumerFactory<String, User> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        config[ConsumerConfig.GROUP_ID_CONFIG] = "group_id"
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory<String, User>(
            config,
            StringDeserializer(),
            JsonDeserializer<User>(User::class.java)
        )
    }

    @Bean
    fun concurrentListenerContainerFactory():ConcurrentKafkaListenerContainerFactory<String, User>{
        val factory:ConcurrentKafkaListenerContainerFactory<String, User> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = userConsumerFactory()
        return factory
    }
}