package controllers.communication;

import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import controllers.security.Secured;

public interface ProviderCommunicationModule {

	@Authenticated(Secured.class)
	Promise<Result> createInstance() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> runInstance() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> stopInstance() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> deleteInstance() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> listInstances() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> createSnapshot() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> restoreSnapshotOrBackup() throws Exception;

	@Authenticated(Secured.class)
	Promise<Result> listSnapshots(String instanceId) throws Exception;
}
