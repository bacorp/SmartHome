package smarthome.center.action;

import smarthome.xively.connector.XivelyConnector;

import com.xively.client.model.Datastream;

public class LightAction extends Action{
	
	public LightAction(){
		super(ActionType.LIGHT_ACTION);
	}

	// execute light action
	// there should be 2 parameters: LIGHT_ACTION_COMMAND AND LIGHT DEVICE ID
	public int execute(String[] args){
		if(args.length != 2){
			// illegal parameter
			return 1;
		}
		
		int command = Integer.parseInt(args[0]);
		String deviceId = args[1];
		
		switch(command){
			case LightActionCommand.TURNON: this.turnOn(deviceId); break;
			case LightActionCommand.TURNOFF: this.turnOff(deviceId);break;
			case LightActionCommand.TURNUP: this.turnUp(deviceId);break;
			case LightActionCommand.TURNDOWN: this.turnDown(deviceId);break;
			default: break;
		}
		
		return 0;
	}
	
	/**
	 * Executive commands to invoke Xively APIs to control Ardiuno
	 * 
	 */
	
	public void turnOn(String deviceId){
		Datastream ds = XivelyConnector.getInstance().readArdiuno(deviceId);
		if(ds != null){
			
		}
	}
    
	public void turnOff(String deviceId){
		
	}
	
	public void turnUp(String deviceId){
		
	}

	public void turnDown(String deviceId){
	
}
}
