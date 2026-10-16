package com.fleetops.CsvParaJson.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.core.Queue;
@Configuration 
public class RabbitConfig {
    public static final String Exchange="retornojson.excharge";
    public  static final String filaVeiculos="jsonVeiculos.queue";
    public static final String filaManifesto="jsonManifesto.queue";
    public static final String routing_key_veiculosJson="retornojsonVeiculos.queue";
    public  static final String routing_key_ManifestoJson="retornoJsonManifesto.queue";
    
    @Bean 
    public DirectExchange encharge(){
        return new DirectExchange(RabbitConfig.Exchange);
    }
    @Bean 
    public Queue filaVeiculos(){
        return QueueBuilder.durable(RabbitConfig.filaVeiculos).build();
    }
    @Bean 
    public Queue filaManifesto(){
        return QueueBuilder.durable(RabbitConfig.filaManifesto).build();
    }
    

    @Bean 
    public Binding bidingManifesto(){
        return BindingBuilder.bind(filaManifesto()).to(encharge()).with(routing_key_ManifestoJson);
    }
    @Bean 
    public Binding bidingVeiculos(){
        return BindingBuilder.bind(filaVeiculos()).to(encharge()).with(routing_key_veiculosJson);
    }

    @Bean
    public MessageConverter messageConverter() {
    return  new JacksonJsonMessageConverter();
}

}
