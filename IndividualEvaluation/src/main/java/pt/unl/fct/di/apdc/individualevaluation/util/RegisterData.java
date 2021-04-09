package pt.unl.fct.di.apdc.individualevaluation.util;

public class RegisterData {

	public String username;
	public String password;
	public String confirmation;
	public String email;
	public String name;
	public String state = "ENABLED";
	public String role = "USER";
	

	public RegisterData() {}
	
	public RegisterData(String username, String password, String confirmation, String email, String name) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.name = name;
	}
	
	public boolean validNames() {
		return !username.equals("") && !name.equals("");
	}
	
	public boolean validPasswordConfirmation() {
		return password.equals(confirmation);
	}
	
	//https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java
	public boolean validPassword() {
		return password.length() >= 8 && password.matches(".*\\d.*");
	}
	
	//https://blog.mailtrap.io/java-email-validation/
	public boolean validEmail() {
		return email.matches("^(.+)@(.+)$");
	}
}