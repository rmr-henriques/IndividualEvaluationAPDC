package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.individualevaluation.util.NewPasswordData;
import pt.unl.fct.di.apdc.individualevaluation.util.UpdateUserData;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateUserResource {
	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();	

	public UpdateUserResource() {} //Nothing to do here

	@PUT
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdate(UpdateUserData data) {
		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenID);
			Entity token = datastore.get(tokenKey);		
			
			if(token == null)
				return exceptions.userNotLoggedIn();
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("token_username"));
			Entity user = datastore.get(userKey);
			
			if(user == null)
				return exceptions.userDoesntExist(token.getString("token_username"));
			
			if(user.getString("user_state").equals("DISABLED")) 
				return exceptions.userIsDisabled();
			
			if(System.currentTimeMillis() >= token.getLong("token_expirationData"))
				return exceptions.sessionExpired();

				user = Entity.newBuilder(user)
						.set("user_profile", data.profile)
						.set("user_phoneNumber", data.phoneNumber)
						.set("user_telephoneNumber", data.telephoneNumber)
						.set("user_address", data.address)
						.set("user_complementaryAddress", data.complementaryAddress)
						.set("user_city", data.city)
						.build();

				txn.update(user);
				txn.commit();
				return Response.ok("Updated Info.").build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@PUT
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(NewPasswordData data) {
		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			if(!data.newPassword.equals(data.confirmation)) 
				return exceptions.passwordsDoNotMatch();
				
			Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenID);
			Entity token = datastore.get(tokenKey);	
		
			if(token == null)
				return exceptions.userNotLoggedIn();
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("token_username"));
			Entity user = datastore.get(userKey);
			
			if(user == null)
				return exceptions.userDoesntExist(token.getString("token_username"));
			
			if(!DigestUtils.sha512Hex(data.oldPassword).equals(user.getString("user_pwd")))
				return exceptions.wrongPassword(token.getString("token_username"));
			
			if(user.getString("user_state").equals("DISABLED")) 
				return exceptions.userIsDisabled();
			
			if(System.currentTimeMillis() >= token.getLong("token_expirationData"))
				return exceptions.sessionExpired();
			
			user = Entity.newBuilder(user)
					.set("user_pwd", DigestUtils.sha512Hex(data.newPassword))
					.build();
			
			txn.update(user);
			txn.commit();
			return Response.ok("Password atualizada.").build();
		}
		finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}