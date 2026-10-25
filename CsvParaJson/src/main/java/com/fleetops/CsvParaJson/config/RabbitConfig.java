package com.fleetops.CsvParaJson.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String Exchange = "veiculo.excharge";
    public static final String routing_key_veiculosJson = "retornojsonVeiculos.queue";
    public static final String routing_key_ManifestoJson = "retornoJsonManifesto.queue";

    @Bean
    public DirectExchange encharge() {
        return new DirectExchange(RabbitConfig.Exchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
