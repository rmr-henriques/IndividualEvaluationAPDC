package pt.unl.fct.di.apdc.individualevaluation.resources;

import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Transaction;

public class ExceptionsResource {

	private Logger LOG;
	private Transaction txn;
	
	public ExceptionsResource(Logger LOG, Transaction txn) {
		this.LOG = LOG;
		this.txn = txn;	
	}
	
	public Response userNotLoggedIn() {
		txn.rollback();
		LOG.warning("User is not logged in.");
		return Response.status(Status.BAD_REQUEST).entity("User is not logged in.").build();
	}
	
	public Response userDoesntExist(String username) {
		txn.rollback();
		LOG.warning("User '"+username+"' does not exist.");
		return Response.status(Status.BAD_REQUEST).entity("User '"+username+"' does not exist.").build();
	}
	
	public Response userIsDisabled() {
		txn.rollback();
		LOG.warning("User is disabled.");
		return Response.status(Status.BAD_REQUEST).entity("User is disabled.").build();
	}
	
	public Response userDoesNotHavePermissions(String username) {
		txn.rollback();
		LOG.warning("User '"+username+"' does not have the permissions to do that.");
		return Response.status(Status.BAD_REQUEST).entity(
				"User '"+username+"' does not have the permissions to do that.").build();
	}
	
	public Response roleIsInvalid() {
		txn.rollback();
		LOG.warning("Invalid role.");
		return Response.status(Status.BAD_REQUEST).entity("Invalid role.").build();
	}
	
	public Response adminCantDeleteItself() {
		txn.rollback();
		LOG.warning("Admin can't delete itself.");
		return Response.status(Status.BAD_REQUEST).entity("Admin can't delete itself.").build();
	}
	
	public Response userIsAlreadyLoggedIn(String username) {
		txn.rollback();
		LOG.warning("'"+username+"' is already logged in!");
		return Response.status(Status.BAD_REQUEST).entity("'"+username+"' is already logged in!").build();
	}
	
	public Response wrongPassword(String username) {
		txn.rollback();
		LOG.warning("Wrong password for username: "+username+".");
		return Response.status(Status.BAD_REQUEST).entity("Wrong password for username: "+username+".").build();
	}
	
	public Response userAlreadyExists(String username) {
		txn.rollback();
		return Response.status(Status.BAD_REQUEST).entity("User '"+username+"' already exists").build();
	}
	
	public Response passwordsDoNotMatch() {
		txn.rollback();
		LOG.warning("Passwords do not match.");
		return Response.status(Status.BAD_REQUEST).entity("Passwords do not match.").build();
	}
	
	public Response sessionExpired() {
		txn.rollback();
		LOG.warning("Your session expired, login again.");
		return Response.status(Status.BAD_REQUEST).entity("Your session expired, login again.").build();
	}
}