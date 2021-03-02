package io.skambo.example.infrastructure.messaging.listener

import io.skambo.example.application.domain.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {

    @KafkaListener(id = "kafkaExampleListener", topics = ["Kafka_Example"], groupId = "group-id")
    fun consume(message:String){
        println("Consumer got this message:$message")
    }

    @KafkaListener(
        id="kafkaExampleJsonListener",
        topics=["Kafka_Example_Json"],
        groupId="group-json",
        containerFactory="concurrentKafkaListenerContainerFactory"
    )
    fun consumeJson(user:User){
        println("Consumer got this json message:$user")
    }
}