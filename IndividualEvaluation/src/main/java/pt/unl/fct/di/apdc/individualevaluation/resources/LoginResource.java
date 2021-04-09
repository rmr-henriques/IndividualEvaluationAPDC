package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.individualevaluation.util.AuthToken;
import pt.unl.fct.di.apdc.individualevaluation.util.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();	
	private Gson g = new Gson();
	
	public LoginResource() {} //Nothing to do here

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);

		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);
			if(user == null) 
				return exceptions.userDoesntExist(data.username);
			
			if(user.getString("user_state").equals("DISABLED"))
				return exceptions.userIsDisabled();
			
			if(!user.getString("user_tokenID").equals("not_logged_in")) 
				return exceptions.userIsAlreadyLoggedIn(data.username);
			
			String hashedPWD = (String) user.getString("user_pwd");
			if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {

				AuthToken t = new AuthToken(data.username);
				
				Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(t.tokenID);
				Entity token = Entity.newBuilder(tokenKey)
						.set("token_username", t.username)
						.set("token_creationData", t.creationData)
						.set("token_expirationData", t.expirationData)
						.set("user_verificationCode",t.verificationCode)
						.build();
				txn.add(token);
				
				user = Entity.newBuilder(user)
						.set("user_tokenID",t.tokenID)
						.build();
				
				txn.update(user);
				txn.commit();
				
				LOG.info("User '"+data.username+"' logged in sucessfully.");
				return Response.ok().entity("User '"+data.username+"' logged in sucessfully."+g.toJson(t)).build();
			}
			else
				return exceptions.wrongPassword(data.username);
		} finally {
			if (txn.isActive()) 
				txn.rollback();
		}
	}
}
