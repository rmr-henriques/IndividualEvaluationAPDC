package pt.unl.fct.di.apdc.individualevaluation.util;

public class ChangeRoleData {
	public String predatorTokenID;
	public String preyUsername;
	public String newRole;

	public ChangeRoleData() {}
	
	public ChangeRoleData(String predatorTokenID, String preyUsername, String newRole) {
		this.predatorTokenID = predatorTokenID;
		this.preyUsername = preyUsername;
		this.newRole = newRole;
	}
	
	public boolean isNewRoleValid() {
		return newRole.equals("USER") || newRole.equals("GBO") || newRole.equals("GA");
	}
}
