package me.security;
import com.pi4j.io.gpio.RaspiPin;

import me.security.hardware.Buzzer;
import me.security.hardware.DisplayElement;

@SuppressWarnings("unused")
public class Test {
	
	public static void main(String[] args) {

		DisplayElement blueLed = new DisplayElement(RaspiPin.GPIO_25);
		blueLed.blinkIndefinitly();
		DisplayElement redLed = new DisplayElement(RaspiPin.GPIO_24);
		redLed.off();
		DisplayElement yellowLed = new DisplayElement(RaspiPin.GPIO_27);
		yellowLed.off();
		DisplayElement greenLed = new DisplayElement(RaspiPin.GPIO_28);
		greenLed.off();

		DisplayElement alarm = new DisplayElement(RaspiPin.GPIO_16);
		Buzzer buzzer = new Buzzer(RaspiPin.GPIO_15);
		
	}
	
	private void mario(Buzzer buzzer) throws InterruptedException {
		buzzer.makeSound(660, 100);
		Thread.sleep(150);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(510, 100);
		Thread.sleep(100);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(770, 100);
		Thread.sleep(550);
		buzzer.makeSound(380, 100);
		Thread.sleep(575);

		buzzer.makeSound(510, 100);
		Thread.sleep(450);
		buzzer.makeSound(380, 100);
		Thread.sleep(400);
		buzzer.makeSound(320, 100);
		Thread.sleep(500);
		buzzer.makeSound(440, 100);
		Thread.sleep(300);
		buzzer.makeSound(480, 80);
		Thread.sleep(330);
		buzzer.makeSound(450, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(200);
		buzzer.makeSound(660, 80);
		Thread.sleep(200);
		buzzer.makeSound(760, 50);
		Thread.sleep(150);
		buzzer.makeSound(860, 100);
		Thread.sleep(300);
		buzzer.makeSound(700, 80);
		Thread.sleep(150);
		buzzer.makeSound(760, 50);
		Thread.sleep(350);
		buzzer.makeSound(660, 80);
		Thread.sleep(300);
		buzzer.makeSound(520, 80);
		Thread.sleep(150);
		buzzer.makeSound(580, 80);
		Thread.sleep(150);
		buzzer.makeSound(480, 80);
		Thread.sleep(500);

		buzzer.makeSound(510, 100);
		Thread.sleep(450);
		buzzer.makeSound(380, 100);
		Thread.sleep(400);
		buzzer.makeSound(320, 100);
		Thread.sleep(500);
		buzzer.makeSound(440, 100);
		Thread.sleep(300);
		buzzer.makeSound(480, 80);
		Thread.sleep(330);
		buzzer.makeSound(450, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(200);
		buzzer.makeSound(660, 80);
		Thread.sleep(200);
		buzzer.makeSound(760, 50);
		Thread.sleep(150);
		buzzer.makeSound(860, 100);
		Thread.sleep(300);
		buzzer.makeSound(700, 80);
		Thread.sleep(150);
		buzzer.makeSound(760, 50);
		Thread.sleep(350);
		buzzer.makeSound(660, 80);
		Thread.sleep(300);
		buzzer.makeSound(520, 80);
		Thread.sleep(150);
		buzzer.makeSound(580, 80);
		Thread.sleep(150);
		buzzer.makeSound(480, 80);
		Thread.sleep(500);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 150);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(100);
		buzzer.makeSound(570, 100);
		Thread.sleep(220);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 200);
		Thread.sleep(300);

		buzzer.makeSound(1020, 80);
		Thread.sleep(300);
		buzzer.makeSound(1020, 80);
		Thread.sleep(150);
		buzzer.makeSound(1020, 80);
		Thread.sleep(300);

		buzzer.makeSound(380, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 150);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(100);
		buzzer.makeSound(570, 100);
		Thread.sleep(420);

		buzzer.makeSound(585, 100);
		Thread.sleep(450);

		buzzer.makeSound(550, 100);
		Thread.sleep(420);

		buzzer.makeSound(500, 100);
		Thread.sleep(360);

		buzzer.makeSound(380, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 150);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(100);
		buzzer.makeSound(570, 100);
		Thread.sleep(220);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 200);
		Thread.sleep(300);

		buzzer.makeSound(1020, 80);
		Thread.sleep(300);
		buzzer.makeSound(1020, 80);
		Thread.sleep(150);
		buzzer.makeSound(1020, 80);
		Thread.sleep(300);

		buzzer.makeSound(380, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(760, 100);
		Thread.sleep(100);
		buzzer.makeSound(720, 100);
		Thread.sleep(150);
		buzzer.makeSound(680, 100);
		Thread.sleep(150);
		buzzer.makeSound(620, 150);
		Thread.sleep(300);

		buzzer.makeSound(650, 150);
		Thread.sleep(300);
		buzzer.makeSound(380, 100);
		Thread.sleep(150);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);

		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(430, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(100);
		buzzer.makeSound(570, 100);
		Thread.sleep(420);

		buzzer.makeSound(585, 100);
		Thread.sleep(450);

		buzzer.makeSound(550, 100);
		Thread.sleep(420);

		buzzer.makeSound(500, 100);
		Thread.sleep(360);

		buzzer.makeSound(380, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);
		buzzer.makeSound(500, 100);
		Thread.sleep(150);
		buzzer.makeSound(500, 100);
		Thread.sleep(300);

		buzzer.makeSound(500, 60);
		Thread.sleep(150);
		buzzer.makeSound(500, 80);
		Thread.sleep(300);
		buzzer.makeSound(500, 60);
		Thread.sleep(350);
		buzzer.makeSound(500, 80);
		Thread.sleep(150);
		buzzer.makeSound(580, 80);
		Thread.sleep(350);
		buzzer.makeSound(660, 80);
		Thread.sleep(150);
		buzzer.makeSound(500, 80);
		Thread.sleep(300);
		buzzer.makeSound(430, 80);
		Thread.sleep(150);
		buzzer.makeSound(380, 80);
		Thread.sleep(600);

		buzzer.makeSound(500, 60);
		Thread.sleep(150);
		buzzer.makeSound(500, 80);
		Thread.sleep(300);
		buzzer.makeSound(500, 60);
		Thread.sleep(350);
		buzzer.makeSound(500, 80);
		Thread.sleep(150);
		buzzer.makeSound(580, 80);
		Thread.sleep(150);
		buzzer.makeSound(660, 80);
		Thread.sleep(550);

		buzzer.makeSound(870, 80);
		Thread.sleep(325);
		buzzer.makeSound(760, 80);
		Thread.sleep(600);

		buzzer.makeSound(500, 60);
		Thread.sleep(150);
		buzzer.makeSound(500, 80);
		Thread.sleep(300);
		buzzer.makeSound(500, 60);
		Thread.sleep(350);
		buzzer.makeSound(500, 80);
		Thread.sleep(150);
		buzzer.makeSound(580, 80);
		Thread.sleep(350);
		buzzer.makeSound(660, 80);
		Thread.sleep(150);
		buzzer.makeSound(500, 80);
		Thread.sleep(300);
		buzzer.makeSound(430, 80);
		Thread.sleep(150);
		buzzer.makeSound(380, 80);
		Thread.sleep(600);

		buzzer.makeSound(660, 100);
		Thread.sleep(150);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(510, 100);
		Thread.sleep(100);
		buzzer.makeSound(660, 100);
		Thread.sleep(300);
		buzzer.makeSound(770, 100);
		Thread.sleep(550);
		buzzer.makeSound(380, 100);
		Thread.sleep(575);
	}
	
	private void christmas(Buzzer buzzer) throws InterruptedException {
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(600);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(600);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(783, 150);
		Thread.sleep(300);
		buzzer.makeSound(523, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(750);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(698, 300);
		Thread.sleep(450);
		buzzer.makeSound(698, 225);
		Thread.sleep(150);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 225);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(525);
		buzzer.makeSound(659, 225);
		Thread.sleep(125);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 750);
		Thread.sleep(675);
		buzzer.makeSound(783, 525);
		Thread.sleep(750);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(600);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(600);
		buzzer.makeSound(659, 150);
		Thread.sleep(300);
		buzzer.makeSound(783, 150);
		Thread.sleep(300);
		buzzer.makeSound(523, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(750);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(698, 300);
		Thread.sleep(450);
		buzzer.makeSound(698, 225);
		Thread.sleep(150);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(659, 225);
		Thread.sleep(300);
		buzzer.makeSound(659, 300);
		Thread.sleep(525);
		buzzer.makeSound(659, 150);
		Thread.sleep(125);
		buzzer.makeSound(783, 150);
		Thread.sleep(300);
		buzzer.makeSound(783, 150);
		Thread.sleep(300);
		buzzer.makeSound(698, 150);
		Thread.sleep(300);
		buzzer.makeSound(587, 150);
		Thread.sleep(300);
		buzzer.makeSound(523, 375);
		Thread.sleep(300);
	}
}
