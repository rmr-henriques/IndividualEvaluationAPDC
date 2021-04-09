package pt.unl.fct.di.apdc.individualevaluation.util;

public class UpdateUserData {
	
	public String tokenID;
	public String profile;
	public String phoneNumber;
	public String telephoneNumber;
	public String address;
	public String complementaryAddress;
	public String city;
	public String postalCode;
	
	public UpdateUserData() {}
	
	public UpdateUserData(String tokenID, String profile, String phoneNumber, String telephoneNumber, 
						  String address, String complementaryAddress, String city, String postalCode) {
		this.tokenID = tokenID;
		this.profile = profile;
		this.phoneNumber = phoneNumber;
		this.telephoneNumber = telephoneNumber;
		this.address = address;
		this.complementaryAddress = complementaryAddress;
		this.city = city;
	}
}
