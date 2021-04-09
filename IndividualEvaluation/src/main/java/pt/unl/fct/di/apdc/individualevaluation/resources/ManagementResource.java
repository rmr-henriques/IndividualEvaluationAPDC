package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.individualevaluation.util.ChangeRoleData;
import pt.unl.fct.di.apdc.individualevaluation.util.ManagementData;

@Path("/management")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ManagementResource {

	//A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private	final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();	
	
	@Context
	private UriInfo uriInfo;

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
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRole(ChangeRoleData data) {
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

			if(predatorRole == 0 || predatorRole == 1 || predatorRole <= prayRole)
				return exceptions.userDoesNotHavePermissions(userPredator.getString("user_role"));

			if(!data.isNewRoleValid())
				return exceptions.roleIsInvalid();
			
			userPray = Entity.newBuilder(userPray)
					.set("user_role",data.newRole)
					.build();

			txn.update(userPray);
			txn.commit();

			return Response.ok("Função atualizada com sucesso.").build();

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@PUT
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateState(ManagementData data) {
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
			
			if(predatorRole == 0 || predatorRole <= prayRole)
				return exceptions.userDoesNotHavePermissions(userPredator.getString("user_role"));
			
			if(userPray.getString("user_state").equals("ENABLED")) {
				userPray = Entity.newBuilder(userPray)
						.set("user_state", "DISABLED")
						.build();
				txn.update(userPray);
				txn.commit();
				return Response.ok("'"+data.preyUsername+"' está agora desativado.").build();
			}
			
			else {
				userPray = Entity.newBuilder(userPray)
						.set("user_state", "ENABLED")
						.build();
				txn.update(userPray);
				txn.commit();
				return Response.ok("'"+data.preyUsername+"' está agora ativado.").build();
			}
		}	finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@PUT
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUser(ManagementData data) {
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
			
			if(predatorRole == 0 || predatorRole <= prayRole && !tokenPredator.getString("token_username").equals(data.preyUsername))
				return exceptions.userDoesNotHavePermissions(userPredator.getString("user_role"));
			
			if(prayRole == 3 && predatorRole == 3)
				return exceptions.adminCantDeleteItself();

			Key tokenKeyPray = datastore.newKeyFactory().setKind("Token").newKey(userPray.getString("user_tokenID"));
			txn.delete(userKeyPray);
			txn.delete(tokenKeyPray);
			txn.commit();
			return Response.ok("'"+data.preyUsername+"' está agora apagado.").build();
			
		}	finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}
