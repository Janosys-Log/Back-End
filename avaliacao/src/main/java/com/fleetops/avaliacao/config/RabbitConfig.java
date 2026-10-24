package com.fleetops.avaliacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "manifesto.consulta.exchange";

    public static final String ROUTING_KEY = "manifesto.existe";

    public static final String FILA = "manifesto.existe.queue";

    @Bean
    public DirectExchange consultaManifestoExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue consultaManifestoQueue() {
        return QueueBuilder.durable(FILA).build();
    }

    @Bean
    public Binding consultaManifestoBinding() {
        return BindingBuilder.bind(consultaManifestoQueue())
                .to(consultaManifestoExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
