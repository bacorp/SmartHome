package smarthome.xively.connector;

import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import smarthome.center.SmartHomeService;

import com.xively.client.XivelyService;
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;

/**
 * Xively Connector: Interaction with Xively with Xively client.
 *
 */
public class XivelyConnector {
	
	private static XivelyConnector instance = null;
	
	// feed and newest data
	private Feed feed;
	private Collection<Datastream> datastreams;
	private Timer timer;
	
	private XivelyConnector(){
	}
	
	public static XivelyConnector getInstance(){
		if(instance == null)
			instance = new XivelyConnector();
		return instance;
	}
	
	
	// initialize the feed
	public void init(){
		this.feed = XivelyService.instance().feed().get(1324195551);
		this.datastreams = feed.getDatastreams();
		this.timer = new Timer();
		timer.schedule(new UpdateTask(), 5000, 5000);
	}
	
	/**
	 * Writer values to Xively in order to change status of Ardiuno
	 * 
	 * @param deviceId
	 * @param value
	 * @return 0 for success, other value for failure
	 */
	public int write2Ardiuno(String deviceId, String value){
		Iterator<Datastream> datastreamsIterator=datastreams.iterator();
        while(datastreamsIterator.hasNext()){           
            Datastream currentDatastream=datastreamsIterator.next();    
            if(deviceId.equalsIgnoreCase(currentDatastream.getId())){
            	currentDatastream.setValue(value);
            	feed = XivelyService.instance().feed().get(1324195551);
            	DatastreamRequester dr = XivelyService.instance().datastream(feed.getId());
            	dr.update(currentDatastream);
            	return 0;
            }
        }
        return -1;
	}
	
	/**
	 * Read data from Xively with specified deviceId
	 * 
	 * @param deviceId
	 * @return
	 */
	public Datastream readArdiuno(String deviceId){
		Iterator<Datastream> datastreamsIterator=datastreams.iterator();
        while(datastreamsIterator.hasNext()){           
            Datastream currentDatastream=datastreamsIterator.next();    
            if(deviceId.equalsIgnoreCase(currentDatastream.getId())){
            	//System.out.println("value:" + currentDatastream.getValue());
            	return currentDatastream;
            }
        }
        return null;
	}
	
	// task for reading updates from Xively
	class UpdateTask extends TimerTask{
		int i = 1;
		@Override
		public void run() {
			try{
			feed = XivelyService.instance().feed().get(1324195551);
			datastreams = feed.getDatastreams();
			SmartHomeService.getInstance().refreshData();
			}catch(Exception e){
				
			}
			// TODO test
			//Datastream test = readArdiuno("light");
			//SmartHomeService.getInstance().tweets2Twitter(new Date().toString() +" " + test.getId() + " - " + test.getValue());
			//int tmp = Integer.parseInt(test.getValue());
			//System.out.println(test.getId() + ", " + test.getValue());
			//write2Ardiuno(test.getId(), ++i +"");
		}
	}
	
	// get all data streams
	public Collection<Datastream> getAllDatastream(){
		return this.datastreams;
	}
	
	
	// only for test
	public static void main(String[] args) {
		XivelyConnector xc = XivelyConnector.getInstance();
		xc.init();
    }
}
