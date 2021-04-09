package pt.unl.fct.di.apdc.individualevaluation.util;

import java.util.Random;
import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 1000*60*20; //20min
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;
	public long verificationCode;
	
	public AuthToken() {} //Nothing to be done here
	
	public AuthToken(String username) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.verificationCode = generateValidationCode();
	}
	
	//https://www.programmersought.com/article/92291268863/
	private int generateValidationCode() {
		int[] array = {0,1,2,3,4,5,6,7,8,9};
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for(int i = 0; i < 6; i++)
			result = result * 10 + array[i];
		return result;
	}
}
