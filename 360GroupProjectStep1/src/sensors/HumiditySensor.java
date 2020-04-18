package sensors;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.TreeSet;

import controller.Controller;
import controller.DataPacket;

public class HumiditySensor extends AbstractSensor implements Runnable {
	private String sensorName = "Humidity";
	private String measurementString = "humidity";
	public static final int MIN_HUMIDITY = 1;
	public static final int MAX_HUMIDITY = 100;
	private static File f = new File("humiditySensorSerializedOutput.txt");
	
	public HumiditySensor(TreeSet<DataPacket<Integer>> outputSet, File f) { 
		super(outputSet, f);
	}
	
	@Override
	public void run() {
		try {
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		eventTime = ZonedDateTime.now();
		DataPacket<Integer> hdp = new DataPacket<Integer>(eventTime, sensorName, measurementString, 
				generateHumidity());
		Controller.humiditySet.add(hdp);
		
		TreeSet<DataPacket<Integer>> humiditySerialize = (TreeSet<DataPacket<Integer>>) 
				 Controller.humiditySet.tailSet(new DataPacket<Integer>(ZonedDateTime
		          .now()
		          .minusSeconds(60), sensorName, measurementString, 0));
	
			try {
				oos.writeObject(humiditySerialize);
				Controller.con.readSerializedData(f);
				oos.flush();
			    oos.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	}
	
	public int generateHumidity() { 
		int randomNumber = (rand.nextInt(MAX_HUMIDITY + 1 - MIN_HUMIDITY)
				+ MIN_HUMIDITY);  
		return randomNumber < 1 ? 1 : randomNumber;
	}
}