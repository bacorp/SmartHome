package smarthome.center.rule;

import com.xively.client.model.Datastream;

import smarthome.center.action.LightAction;
import smarthome.xively.connector.XivelyConnector;

// A simple rule for get-up scenario
// if light value is larger or equals to 5, turn on LED 1
public class SimpleRule extends Rule{
	
	private double threhold;
	
	private LightAction lightAction;
	
	private String targetId;
	
	public SimpleRule(int value){
		this.threhold = value;
		this.lightAction = new LightAction();
		this.targetId = "light";
		this.ruleName = "SimpleRule";
	}
	
	public void check(){
		
		Datastream ds = XivelyConnector.getInstance().readArdiuno(this.targetId);
		if(ds != null){
			double currentValue = Double.parseDouble(ds.getValue());
			System.out.println("Simple rule check, current value: " + currentValue + ", threhold: " + this.threhold);
			if(currentValue >= threhold){
				System.out.println("Turn up LED");
				lightAction.turnUp("LED");
			}else{
				System.out.println("Turn down LED");
				lightAction.turnDown("LED");
			}
			
		}else{
			System.out.println("Cannot obtain value of " + this.targetId);
		}
		
	}
	
	public void setThrehold(double value){
		this.threhold = value;
	}
	
}
