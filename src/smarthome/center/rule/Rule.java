package smarthome.center.rule;

/**
 * Rule class for representing the rule used in smart home system.
 * Rules are used to control devices according to situation.
 * Scenario can be implemented by specifying a set of rules.
 * @author
 *
 */
public class Rule {

	// name of this rule, such as 'control-temperature'
	protected String ruleName;
	
	public Rule(){
		
	}

	public Rule(String ruleName) {
		super();
		this.ruleName = ruleName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public void check(){
		// TODO implemented by child classes
	}
	
	
}
