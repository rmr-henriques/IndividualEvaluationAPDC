package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.individualevaluation.util.ManagementData;

@Path("/moderator")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModeratorResource {

	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();	
	private final Gson g = new Gson();
	
	private int getRoleCode(String role) {
		if(role.equals("USER"))
			return 0;
		if(role.equals("GBO"))
			return 1;
		if(role.equals("GA"))
			return 2;
		if(role.equals("SU"))
			return 3;
		else
			return -1;
	}	

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUserInfo(ManagementData data) {
		
		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			Key tokenKeyPredator = datastore.newKeyFactory().setKind("Token").newKey(data.predatorTokenID);
			Entity tokenPredator = datastore.get(tokenKeyPredator);
			
			if(tokenPredator == null)
				exceptions.userNotLoggedIn();
			
			Key userKeyPray = datastore.newKeyFactory().setKind("User").newKey(data.preyUsername);
			Entity userPray = datastore.get(userKeyPray);
			
			if(userPray == null) 
				return exceptions.userDoesntExist(data.preyUsername);

			Key userKeyPredator = datastore.newKeyFactory().setKind("User").newKey(tokenPredator.getString("token_username"));
			Entity userPredator= datastore.get(userKeyPredator);

			if(userPredator == null)
				return exceptions.userDoesntExist(tokenPredator.getString("token_username"));
			
			if(userPredator.getString("user_state").equals("DISABLED"))
				return exceptions.userIsDisabled();
			
			if(System.currentTimeMillis() >= tokenPredator.getLong("token_expirationData"))
				return exceptions.sessionExpired();
			
			int predatorRole = getRoleCode(userPredator.getString("user_role"));
			int prayRole = getRoleCode(userPray.getString("user_role"));

			if(predatorRole == 0 || predatorRole <= prayRole) {
				return exceptions.userDoesNotHavePermissions(tokenPredator.getString("token_username"));
			}
			else {
				txn.commit();
				DataShower ds = new DataShower(data.preyUsername, userPray.getString("user_name")
											   , userPray.getString("user_email"), userPray.getString("user_role")
											   , userPray.getString("user_state"), userPray.getString("user_profile")
											   , userPray.getString("user_phoneNumber"), userPray.getString("user_telephoneNumber")
											   , userPray.getString("user_address"),  userPray.getString("user_complementaryAddress")
											   , userPray.getString("user_city"));
				return Response.ok(g.toJson(ds)).build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
	private class DataShower {
		@SuppressWarnings("unused")
		public String username;
		@SuppressWarnings("unused")
		public String name;
		@SuppressWarnings("unused")
		public String email;
		@SuppressWarnings("unused")
		public String role;
		@SuppressWarnings("unused")
		public String state;
		@SuppressWarnings("unused")
		public String profile;
		@SuppressWarnings("unused")
		public String phoneNumber;
		@SuppressWarnings("unused")
		public String telephoneNumber;
		@SuppressWarnings("unused")
		public String address;
		@SuppressWarnings("unused")
		public String complementaryAddress;
		@SuppressWarnings("unused")
		public String city;

		public DataShower(String username, String name, String email, String role, String state,
				String profile, String phoneNumber, String telephoneNumber, String address, String complementaryAddress, String city) {
			this.username = username;
			this.name = name;
			this.email = email;
			this.role = role;
			this.state = state;
			this.profile = profile;
			this.phoneNumber = phoneNumber;
			this.telephoneNumber = telephoneNumber;
			this.address = address;
			this.complementaryAddress = complementaryAddress;
			this.city = city;
		}
	}
}
