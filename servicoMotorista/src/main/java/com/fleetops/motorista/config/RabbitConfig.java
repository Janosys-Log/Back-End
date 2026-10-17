package com.fleetops.motorista.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
@Configuration 
public class RabbitConfig {
    public static final  String fila="jsonVeiculos.queue";
    public static final String Excharge="veiculo.excharge";
    public static final String routing_key_veiculosJson="retornojsonVeiculos.queue";

     
    @Bean 
    public DirectExchange excharge(){
        return new DirectExchange(RabbitConfig.Excharge);
    }
    

    @Bean 
    public Queue Fila(){
        return QueueBuilder.durable(fila).build();
    }

    @Bean 
    public Binding bidingVeiculos(){
        return BindingBuilder.bind(Fila()).to(excharge()).with(routing_key_veiculosJson);
    }

      @Bean
    public MessageConverter messageConverter() {
    return  new JacksonJsonMessageConverter();
}



}
