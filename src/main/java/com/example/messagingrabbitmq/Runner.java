package com.example.messagingrabbitmq;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private final RabbitTemplate rabbitTemplate;
	// Spring AMQP RabbitTemplate для отправки и получения сообщений(получаем из контекста приложения) через RabbitMQ.
	// Spring Boot автоматически создает фабрику соединения и RabbitTemplate,
	private final Receiver receiver;

	public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
		this.receiver = receiver;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in); // считываем все что вводится в консоли
		while (true) {
			String message = scanner.nextLine(); // записываем строку в переменную
			if (message.equals("q")) break; // если в строке есть символ q, то завершаем программу
			if (message.isBlank()) continue; // если пусто продолжаем
			rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.fanoutExchangeName, "", message);
			receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
		}
	}

}
