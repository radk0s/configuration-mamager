package controllers.communication;

import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import controllers.security.Secured;

public interface ProviderCommunicationModule {

	@Authenticated(Secured.class)
	Promise<Result> createInstance();

	@Authenticated(Secured.class)
	Promise<Result> runInstance();

	@Authenticated(Secured.class)
	Promise<Result> stopInstance();

	@Authenticated(Secured.class)
	Promise<Result> deleteInstance();
	
	@Authenticated(Secured.class)
	Promise<Result> listInstances();	
}
