package smarthome.center.action;

/**
 * Action class represents for actions in smart home system.
 * Action is triggered by rules or users' command.
 * Actions varies in types, such as Light action, Heater action...
 * 
 * @author
 *
 */
public abstract class Action {

	// action type
	protected String actionType;
	
	public Action(){
		super();
	}

	public Action(String actionType) {
		super();
		this.actionType = actionType;
	}
	
	// leave to child classes implementation
	// return value: 0 for success, 1 for illegal parameter, -1 for execution failure
	public abstract int execute(String[] args);

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
}
