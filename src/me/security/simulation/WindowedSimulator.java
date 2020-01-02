package me.security.simulation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.PinEvent;

import me.security.hardware.Digicode;
import me.security.managers.SecuManager;
import utils.JUnitGPIO;

@SuppressWarnings("serial")
public class WindowedSimulator extends JFrame {
	
	private JLabel notificationLabel = new JLabel("Liste des notifications :");
	
	public List<String> notifs = new ArrayList<String>();

	public WindowedSimulator(SecuManager secu) {
		super("Simulateur");
		final GpioProvider provider = GpioFactory.getDefaultProvider();
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.anchor = GridBagConstraints.CENTER;
		mainConstraints.insets = new Insets(10, 10, 10, 10);
		mainConstraints.gridx = 0;
		mainConstraints.gridy = 0;
		this.add(mainPanel);

		JPanel digicode = new JPanel(new GridBagLayout());
		digicode.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Digicode"));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 10, 10, 10);

		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				constraints.gridx = x;
				constraints.gridy = y;
				JButton digiButton = new JButton(Digicode.KEYS[y][x] + "");
				
				final int tmpX = x;
				final int tmpY = y;
				digiButton.addActionListener((ActionEvent e) -> {
					JUnitGPIO.pressDigicode(JUnitGPIO.columns[tmpX], JUnitGPIO.lines[tmpY]);
				});
				digicode.add(digiButton, constraints);
			}
		}

		mainPanel.add(digicode, mainConstraints);
		
		JPanel displayers = new JPanel(new GridBagLayout());
		displayers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Affichage"));

		constraints.gridx = 0;
		constraints.gridy = 0;
		final JLabel red = new JLabel(createImageIcon("/leds/off.png"));
		provider.addListener(RaspiPin.GPIO_24, (PinEvent event) -> {
			switch(provider.getState(event.getPin())) {
			case HIGH:
				red.setIcon(createImageIcon("/leds/red.png"));
				break;
			case LOW:
				red.setIcon(createImageIcon("/leds/off.png"));
				break;
			}
		});
		displayers.add(red, constraints);
		
		constraints.gridx++;
		JLabel green = new JLabel(createImageIcon("/leds/off.png"));
		provider.addListener(RaspiPin.GPIO_28, (PinEvent event) -> {
			switch(provider.getState(event.getPin())) {
			case HIGH:
				green.setIcon(createImageIcon("/leds/green.png"));
				break;
			case LOW:
				green.setIcon(createImageIcon("/leds/off.png"));
				break;
			}
		});
		displayers.add(green, constraints);
		
		constraints.gridx++;
		JLabel yellow = new JLabel(createImageIcon("/leds/off.png"));
		provider.addListener(RaspiPin.GPIO_27, (PinEvent event) -> {
			switch(provider.getState(event.getPin())) {
			case HIGH:
				yellow.setIcon(createImageIcon("/leds/yellow.png"));
				break;
			case LOW:
				yellow.setIcon(createImageIcon("/leds/off.png"));
				break;
			}
		});
		displayers.add(yellow, constraints);
		
		constraints.gridx++;
		JLabel blue = new JLabel(createImageIcon("/leds/off.png"));
		provider.addListener(RaspiPin.GPIO_25, (PinEvent event) -> {
			switch(provider.getState(event.getPin())) {
			case HIGH:
				blue.setIcon(createImageIcon("/leds/blue.png"));
				break;
			case LOW:
				blue.setIcon(createImageIcon("/leds/off.png"));
				break;
			}
		});
		displayers.add(blue, constraints);
		
		constraints.gridx++;
		JLabel alarm = new JLabel(createImageIcon("/alarm/off.png"));
		provider.addListener(RaspiPin.GPIO_16, (PinEvent event) -> {
			switch(provider.getState(event.getPin())) {
			case HIGH:
				alarm.setIcon(createImageIcon("/alarm/on.png"));
				break;
			case LOW:
				alarm.setIcon(createImageIcon("/alarm/off.png"));
				break;
			}
		});
		displayers.add(alarm);

		mainConstraints.gridx++;
		mainPanel.add(displayers, mainConstraints);
		
		JPanel triggers = new JPanel(new GridBagLayout());
		triggers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Déclencheurs"));

		/*constraints.gridx = 0;
		constraints.gridy = 0;
		JButton heat = new JButton(createImageIcon("/triggers/heat.png"));
		heat.addActionListener((ActionEvent e) -> {
		});
		triggers.add(heat, constraints);*/
		
		constraints.gridx++;
		JButton motion = new JButton(createImageIcon("/triggers/motion.png"));
		motion.addActionListener((ActionEvent e) -> {
			provider.setState(RaspiPin.GPIO_04, PinState.HIGH);
			provider.setState(RaspiPin.GPIO_04, PinState.LOW);
		});
		triggers.add(motion, constraints);
		
		constraints.gridx++;
		JButton window = new JButton(createImageIcon("/triggers/window.png"));
		window.addActionListener((ActionEvent e) -> {
			provider.setState(RaspiPin.GPIO_07, PinState.HIGH);
			provider.setState(RaspiPin.GPIO_07, PinState.LOW);
		});
		triggers.add(window, constraints);

		mainConstraints.gridx++;
		mainPanel.add(triggers, mainConstraints);
		

		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void updateNotificationLabel() {
		String preparedString = "Notifications :\n";
		for(String notif : notifs) {
			preparedString += notif + "\n";
		}
		this.notificationLabel.setText(preparedString);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}

		System.err.println("Couldn't find file: " + path);
		return null;
	}

}