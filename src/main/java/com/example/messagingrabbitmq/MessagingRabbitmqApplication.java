package com.example.messagingrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class MessagingRabbitmqApplication {

	static final String fanoutExchangeName = "spring-boot-exchange";
	// fanoutExchange возможность осуществления выборочной маршрутизации путем сравнения ключа маршрутизации

	static final String queueName = "spring-boot";

	@Bean
	Queue queue() {
		return new Queue(queueName + UUID.randomUUID(), false, false, true );
	}
	// генерация уникального имени для каждой новой очереди
	// durable - false(exchange является временным и будет удаляться, когда сервер/брокер будет перезагружен)
	// exclusive - false(очередь разрешает подключаться только одному потребителю и удаляется если закроется канал)
	// autoDelete - true(Exchange будет удален, когда будут удалены все связанные с ним очереди)
	// Для каждого клиента вам понадобится эксклюзивная автоудаляемая очередь с уникальным названием

	@Bean
	FanoutExchange exchange() {
		return new FanoutExchange(fanoutExchangeName);
	}
	// Для того чтобы сообщение рассылалось во все очереди вам понадобится FanoutExchange

	@Bean
	Binding binding(Queue queue, FanoutExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange);
	}
	// создание связи(binding) к обменнику(exchange)

	@Bean
	SimpleMessageListenerContainer container // Listener container для асинхронной обработки входящих сообщений
			(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory); // устанавливает соединение с rabbitmq
		container.setQueues(queue()); // устанавливает все известные очереди, для которых нужна обработка
		container.setMessageListener(listenerAdapter); // устанавливает целевой метод обработки сообщений
		return container;
	}

	@Bean // данный метод - обработчик сообщения в контейнере (подсоединяется выше)
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
		// для того, чтобы Receiver работал - упаковываем его
		// и вызываем в нем метод receiveMessage для обработки сообщений
		// для того, чтобы
	}

	public static void main(String[] args) {
		SpringApplication.run(MessagingRabbitmqApplication.class, args).close();
	}

}
