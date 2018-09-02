package arthur.feedingControl.device;

import org.apache.log4j.Logger;




import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class DeviceControl {
	static Logger log = Logger.getLogger(DeviceControl.class);
	static final GpioController gpio = GpioFactory.getInstance();
	/**
	 * @param mainNo
	 * @param machineNo
	 * @param time  second
	 * @throws InterruptedException 
	 */
	public void openMachine(int pinNo,int time) throws InterruptedException{
		log.info("openMachine method");
	    try {
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "machine", PinState.HIGH);
					pin.high();
				    try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			       	pin.low();
			       	gpio.unprovisionPin(pin);
				}
			}).start();
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "machine", PinState.HIGH);
					pin.high();
				    try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			       	pin.low();
			       	gpio.unprovisionPin(pin);
				}
			}).start();
		} catch (Exception e) {
		}
	    
	}
}
