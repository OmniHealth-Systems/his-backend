package com.clinic.notificacionesservice.config;

import com.clinic.notificacionesservice.DTO.CitaEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer; // Importación faltante
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, CitaEvent> citaEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(CitaEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CitaEvent> citaEventListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CitaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(citaEventConsumerFactory());
        return factory;
    }

    // Configuración similar para PagoEvent
}