package smarthome.twitter.connector;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

import com.xively.client.XivelyService;
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;

import smarthome.center.SmartHomeService;
import smarthome.xively.connector.XivelyConnector;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/**
 * TwitterConnector is used for interacting with Twitter server
 * 
 * Read tweets of hashtag #smarthome# and publish status updates
 * 
 */
public final class TwitterConnector {
	
	private static TwitterConnector instance = null;

	private final String hashTag = "#smarthome#";
	
	// instance of Twitter
	private Twitter twitter;
	private TwitterStream twitterStream;
	//private RequestToken requestToken;
	private AccessToken accessToken;
	
	private TwitterConnector(){	}
	
	public static TwitterConnector getInstance(){
		if(instance == null)
			instance = new TwitterConnector();
		return instance;
	}
	
	// TODO Fill these keys
	public static final int userId = 1001010;
	public static final String consumerKey = "";
	public static final String consumerSecret = "";
	public static final String accessKey = "";
	public static final String accessKeySecret = "";

	/**
	 * Publish status updates to Twitter
	 * 
	 * @param message
	 * @throws TwitterException
	 */
	public void publish(String message) throws TwitterException {		
		
		try {

			Status status = twitter.updateStatus(message);
			System.out.println("Successfully updated the status to ["
					+ status.getText() + "].");
			//System.exit(0);
		} catch (TwitterException te) {
			//te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
			//System.exit(-1);
		}
	}
	
	
	// listen to Twitter
    StatusListener listener = new StatusListener() {

		@Override
		public void onException(Exception arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatus(Status arg0) {
			String message = arg0.getText();
			System.out.println("Received user command:" + message);		
			if(message.indexOf(hashTag) > -1){
				message = message.substring(message.indexOf(hashTag) + hashTag.length() + 1);
				String[] commands = message.split("\\s"); // #smarthome# LED:1 light:0
				System.out.println("commands length:" + commands.length);
				for(int i = 0; i < commands.length; ++i){
					String[] command = commands[i].split(":");
					String deviceId = command[0];
					String value = command[1];
					if(deviceId.startsWith("led") || deviceId.equals("light")){
						System.out.println("execute command: deviceid " + deviceId + ", value " + value);
						SmartHomeService.getInstance().command2Xively(deviceId, value);
					}else if(deviceId.startsWith("get")){
						//System.out.println("execute command: tweet the state of " + value );
						System.out.println("execute command: tweet the state of " + value + " : " + XivelyConnector.getInstance().readArdiuno(value).getValue());
						
						String tweet = "[" + new Date().toString() + "]" + value +" : " + XivelyConnector.getInstance().readArdiuno(value).getValue();
						try {
							twitter.updateStatus(tweet);
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
						/*
						Status status = twitter.updateStatus(message);
						System.out.println("Successfully updated the status to ["
								+ status.getText() + "].");
						*/
					}else if(deviceId.startsWith("all-leds")){
						System.out.println("execute command: all-leds: " + value );
//						Collection<Datastream> dataStreams = XivelyConnector.getInstance().getAllDatastream();
//						Iterator<Datastream> datastreamsIterator=dataStreams.iterator();
//						String newValue = "1";
						if(value.equalsIgnoreCase("on")){				
//					        newValue = "1";
							XivelyConnector.getInstance().write2Ardiuno("LED1", "1");
							XivelyConnector.getInstance().write2Ardiuno("LED2", "1");
							XivelyConnector.getInstance().write2Ardiuno("LED3", "1");
							XivelyConnector.getInstance().write2Ardiuno("LED4", "1");
						}else if(value.equalsIgnoreCase("off")){
//							newValue = "0";
							XivelyConnector.getInstance().write2Ardiuno("LED1", "0");
							XivelyConnector.getInstance().write2Ardiuno("LED2", "0");
							XivelyConnector.getInstance().write2Ardiuno("LED3", "0");
							XivelyConnector.getInstance().write2Ardiuno("LED4", "0");
						}else{
							return;
						}
						
						//Feed feed = XivelyService.instance().feed().get(1324195551);
		            	//DatastreamRequester dr = XivelyService.instance().datastream(feed.getId());
//						while(datastreamsIterator.hasNext()){           
//				            Datastream currentDatastream=datastreamsIterator.next();    
//				            if(currentDatastream.getId().startsWith("LED")){
//				            	System.out.println("include " + currentDatastream.getId());
//				            	currentDatastream.setValue(newValue);
//				            	dr.update(currentDatastream);
//				            }
//				        }
						
						System.out.println("turn " + value + " all leds, OK!");
					}else if(deviceId.equals("SimpleRule")){
						System.out.println("adjust the rule parameter rule " + deviceId + ", threhold " + value);
						SmartHomeService.getInstance().setRulePara(Double.parseDouble(value));;
					}else{
						System.out.println("unrecognized command " + deviceId + ":" + value);
					}
					
					
				}
			}else{
				System.out.println("not smart home command");
			}
		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
			// TODO Auto-generated method stub
			
		}
        
    };

    
	
	// Get all rights by OAuth
	public void init(){
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(consumerKey);
		configurationBuilder.setOAuthConsumerSecret(consumerSecret);
		configurationBuilder.setJSONStoreEnabled(true);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		TwitterStreamFactory tf = new TwitterStreamFactory(configuration); 
		twitterStream = tf.getInstance();
		accessToken = new AccessToken(accessKey, accessKeySecret);
		System.out.println("AccessToken:" + accessToken);
		twitter.setOAuthAccessToken(accessToken);
		twitterStream.setOAuthAccessToken(accessToken);
		
		
		twitterStream.addListener(listener);
	    FilterQuery query = new FilterQuery();
	    query.follow(new long[] { userId });
	    twitterStream.filter(query);
	}

	// only for test
	public static void main(String... args) throws TwitterException{
//		TwitterConnector tc = TwitterConnector.getInstance();
//		tc.init();
//		tc.publish(new Date().toString() + " Smart Home Test Tweets");
		
		String message = "led1:1";
		String[] commands = message.split("\\s"); 
		System.out.println(commands[0]);
		System.out.println(commands[1]);
	}
	
}
