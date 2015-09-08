package smarthome.center;

import java.util.Collection;

import com.xively.client.model.Datastream;

import smarthome.center.rule.Rule;
import smarthome.center.rule.SimpleRule;
import smarthome.twitter.connector.TwitterConnector;
import smarthome.xively.connector.XivelyConnector;
import twitter4j.TwitterException;

/**
 * Singleton - Used for other modules for interacting with Xively and Twitter.
 * @author 
 */
public class SmartHomeService {
	
	// singleton
	private static SmartHomeService instance = null;
	
	// connectors for twitter and xively
	private XivelyConnector xivelyConnector;
	private TwitterConnector twitterConnector;
	
	// rule list
	private SimpleRule rule;
	
	private SmartHomeService(){
		rule = new SimpleRule(5);
		xivelyConnector = XivelyConnector.getInstance();
		twitterConnector = TwitterConnector.getInstance();
		xivelyConnector.init();
		twitterConnector.init();
	}
	
	public static SmartHomeService getInstance(){
		if(instance == null){
			instance = new SmartHomeService();
		}
		return instance;
	}
	
	// get all data streams from xively
	public Collection<Datastream> getAllDatastream(){
		return xivelyConnector.getAllDatastream();
	}
	
	// write commands to Xively to control Ardiuno
	public void command2Xively(String deviceId, String value){
		xivelyConnector.write2Ardiuno(deviceId, value);
	}
	
	// publish a tweet on Twitter
	public void tweets2Twitter(String tw){
		try {
			this.twitterConnector.publish(tw);
		} catch (TwitterException e) {
			//e.printStackTrace();
		}
	}
	
	// Xively update current data by invoking this method
	public void refreshData(){
		this.rule.check();
	}
	
	// User command to set rule
	public void setRulePara(double value){
		this.rule.setThrehold(value);
	}
	
	// main program entry
	public static void main(String...args){
		SmartHomeService shs = SmartHomeService.getInstance();
	}
	
}
