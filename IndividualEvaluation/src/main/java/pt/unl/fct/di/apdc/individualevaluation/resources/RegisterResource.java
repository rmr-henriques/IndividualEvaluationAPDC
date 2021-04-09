package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.individualevaluation.util.RegisterData;

@Path("register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {} //Nothing to do here

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegister(RegisterData data) {
		LOG.fine("Login attempt by user: " + data.username);

		//Parameters validation
		if(!data.validPassword())
			return Response.status(Status.BAD_REQUEST).entity("Password needs to contain 8 characters and at least one number.").build();

		if(!data.validPasswordConfirmation())
			return Response.status(Status.BAD_REQUEST).entity("Passwords do not match.").build();

		if(!data.validEmail())
			return Response.status(Status.BAD_REQUEST).entity("Invalid email.").build();

		if(!data.validNames())
			return Response.status(Status.BAD_REQUEST).entity("Empty username or name.").build();

		Transaction txn = datastore.newTransaction();
		ExceptionsResource exceptions = new ExceptionsResource(LOG, txn);
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(user != null)
				return exceptions.userAlreadyExists(data.username);

			user = Entity.newBuilder(userKey)
					.set("user_name", data.name)
					.set("user_pwd", DigestUtils.sha512Hex(data.password))
					.set("user_email", data.email)
					.set("user_role", data.role)
					.set("user_creation_time",Timestamp.now())
					.set("user_state", data.state)
					.set("user_tokenID", "not_logged_in")
					.set("user_profile", "PUBLIC")
					.set("user_phoneNumber", "")
					.set("user_telephoneNumber", "")
					.set("user_address", "")
					.set("user_complementaryAddress", "")
					.set("user_city", "")
					.build();
			txn.add(user);
			LOG.info("User registered" + data.username);
			txn.commit();
			return Response.ok("Bem vindo "+data.name+"!").build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}
