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
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.individualevaluation.util.LogoutData;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();	
	private KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("Token");
	
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(LogoutData data) {
		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			Key tokenKey = userKeyFactory.newKey(data.tokenID);
			Entity token = datastore.get(tokenKey);
			
			if(token == null)
				return exceptions.userNotLoggedIn();
			
			String name = token.getString("token_username");
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(name);
			Entity user = datastore.get(userKey);
			
			user = Entity.newBuilder(user)
					.set("user_tokenID","not_logged_in")
					.build();
			
			txn.delete(tokenKey);
			txn.update(user);
			txn.commit();
			LOG.info("User '"+name+"' logged out sucessfully.");
			return Response.ok().entity("User '"+name+"' logged out sucessfully.").build();
		} finally {
			if (txn.isActive()) 
				txn.rollback();
		}
	}
}
