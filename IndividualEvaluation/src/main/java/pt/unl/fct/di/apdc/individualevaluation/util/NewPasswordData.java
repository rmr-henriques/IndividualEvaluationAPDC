package pt.unl.fct.di.apdc.individualevaluation.util;

public class NewPasswordData {

	public String newPassword;
	public String confirmation;
	public String tokenID;
	public String oldPassword;
	
	public NewPasswordData() {}
	
	public NewPasswordData(String tokenID, String newPassword, String confirmation, String oldPassword) {
		this.tokenID = tokenID;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmation = confirmation;
	}
	
}
