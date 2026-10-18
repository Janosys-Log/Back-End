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
    public static final  String filaVeiculo="jsonVeiculos.queue";
    public static  final String filaManifesto="manifesto.queue";
    public static final String Excharge="veiculo.excharge";
    public static final String routing_key_veiculosJson="retornojsonVeiculos.queue";
    public  static final String routing_key_ManifestoJson="retornoJsonManifesto.queue";

     
    @Bean 
    public DirectExchange excharge(){
        return new DirectExchange(RabbitConfig.Excharge);
    }
    

    @Bean 
    public Queue filaVeiculo(){
        return QueueBuilder.durable(filaVeiculo).build();
    }
    @Bean  Queue filaManifesto(){
        return QueueBuilder.durable(filaManifesto).build();
    }
  
    @Bean 
    public Binding bidingVeiculos(){
        return BindingBuilder.bind(filaVeiculo()).to(excharge()).with(routing_key_veiculosJson);
    }

    @Bean 
    public Binding bidingManifesto(){
        return BindingBuilder.bind(filaManifesto()).to(excharge()).with(routing_key_ManifestoJson);
    }



   

      @Bean
    public MessageConverter messageConverter() {
    return  new JacksonJsonMessageConverter();
}



}
