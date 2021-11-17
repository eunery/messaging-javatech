package com.example.messagingrabbitmq;

import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	private CountDownLatch latch = new CountDownLatch(1);
	// поток исполнения должен находиться в состоянии ожидания до тех пор,
	// пока не произойдет одно или несколько событий, а именно пока счетчик не станет равен 0

	public void receiveMessage(String message) {
		System.out.println("Received <" + message + ">");
		latch.countDown(); // понижаем значение счетчика
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
