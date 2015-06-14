package controllers.communication;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import controllers.security.Secured;

public class DigitalOceanCommunicationModule extends Controller implements ProviderCommunicationModule {

	@Inject
	private UserPersistenceService userService;

	private static Function<WSResponse, Result> function;
	static {
		function = new Function<WSResponse, Result>() {
			public Result apply(WSResponse response) {
				JsonNode json = response.asJson();
				return ok(json);
			}
		};
	}

	/**
	 * REQUEST BODY example: { "name": "example.com", "region": "nyc3", "size": "512mb", "image": "ubuntu-14-04-x64",
	 * "ssh_keys": null, "backups": false, "ipv6": * true, "user_data": null, "private_networking": null }
	 * 
	 * Documentation: https://developers.digitalocean.com/documentation/v2/#create-a-new-droplet
	 */
	@Override
	public Promise<Result> createInstance() {
		WSRequestHolder request = createRequest(Urls.DIGITAL_OCEAN_URL.toString());
		return request.post(request().body().asJson()).map(function);
	}

	/**
	 * REQUEST BODY example: { "instanceId": "id" }
	 * 
	 * Documentation: https://developers.digitalocean.com/documentation/v2/#power-on-a-droplet
	 */
	@Override
	public Promise<Result> runInstance() {
		ObjectNode body = Json.newObject().put("type", "power_on");
		return dropletActions(body);
	}

	/**
	 * REQUEST BODY example: { "instanceId": "id" }
	 * 
	 * Documentation: https://developers.digitalocean.com/documentation/v2/#shutdown-a-droplet
	 */
	@Override
	public Promise<Result> stopInstance() {
		ObjectNode body = Json.newObject().put("type", "shutdown");
		return dropletActions(body);
	}

	/**
	 * REQUEST BODY example: { "instanceId": "id" }
	 * 
	 * Documentation: https://developers.digitalocean.com/documentation/v2/#delete-a-droplet
	 */
	@Override
	public Promise<Result> deleteInstance() {
		String url = Urls.DIGITAL_OCEAN_URL.toString() + getInstanceId();
		WSRequestHolder request = createRequest(url);
		return request.delete().map(function);
	}

	/**
	 * Documentation: https://developers.digitalocean.com/documentation/v2/#list-all-droplets
	 */
	@Override
	public Promise<Result> listInstances() {
		WSRequestHolder request = createRequest(Urls.DIGITAL_OCEAN_URL.toString());
		return request.get().map(function);
	}


	/**
	 * REQUEST BODY EXAMPLE : {"type": "snapshot", "name": "Nifty New Snapshot"}
	 *
	 * Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#snapshot-a-droplet
	 */
	@Override
	public Promise<Result> createSnapshot() throws Exception {
		final String snapshotName = request().body().asJson().get("name").asText();
		ObjectNode body = Json.newObject().put("type", "snapshot").put("name", snapshotName);
		return dropletActions(body);
	}

	/**
	 * Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#disable-backups
	 */
	@Security.Authenticated(Secured.class)
	public Promise<Result> disableBackups() throws Exception {
		ObjectNode body = Json.newObject().put("type", "disable_backups");
		return dropletActions(body);
	}

	/**
	 * Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#restore-a-droplet
	 */
	@Override
	public Promise<Result> restoreSnapshotOrBackup() throws Exception {
		final int imageId = request().body().asJson().get("image").asInt();
		ObjectNode body = Json.newObject().put("type", "restore").put("image", imageId);
		return dropletActions(body);
	}

	/**
	 * Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#list-snapshots-for-a-droplet
	 */
	@Override
	public Promise<Result> listSnapshots(String instanceId) throws Exception {
		return listBackupsOrSnapshots(instanceId, "snapshots");
	}

	/**
	 * Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#list-backups-for-a-droplet
	 */
	@Security.Authenticated(Secured.class)
	public Promise<Result> listBackups(String instanceId) throws Exception {
		return listBackupsOrSnapshots(instanceId, "backups");
	}

	/**
	 * List all regions for Digital Ocean VMs Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#list-all-regions
	 */
	@Security.Authenticated(Secured.class)
	public Promise<Result> listRegions() {
		WSRequestHolder request = createRequest(Urls.DIGITAL_OCEAN_REGIONS.toString());
		return request.get().map(function);
	}

	/**
	 * List all images for Digital Ocean VMs Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#list-all-distribution-images
	 */
	@Security.Authenticated(Secured.class)
	public Promise<Result> listImages() {
		WSRequestHolder request = createRequest(Urls.DIGITAL_OCEAN_IMAGES.toString());
		return request.get().map(function);
	}

	/**
	 * List all sizes for Digital Ocean VMs Documentation:
	 * https://developers.digitalocean.com/documentation/v2/#list-all-sizes
	 */
	@Security.Authenticated(Secured.class)
	public Promise<Result> listSizes() {
		WSRequestHolder request = createRequest(Urls.DIGITAL_OCEAN_SIZES.toString());
		return request.get().map(function);
	}

	private Promise<Result> dropletActions(ObjectNode body) {
		int instanceId = getInstanceId();
		String url = Urls.DIGITAL_OCEAN_URL.toString() + instanceId + "/actions";

		WSRequestHolder request = createRequest(url);
		return request.post(body).map(function);
	}

	private int getInstanceId() {
		int instanceId = request().body().asJson().get("instanceId").asInt();
		return instanceId;
	}

	private Promise<Result> listBackupsOrSnapshots(String instanceId, String type) {
		String url = Urls.DIGITAL_OCEAN_URL.toString() + instanceId + "/" + type;
		WSRequestHolder request = createRequest(url);
		return request.get().map(function);
	}

	private WSRequestHolder createRequest(String url) {
		WSRequestHolder request = WS.url(url);
		User user = getUserFromRequest();

		request.setHeader("Authorization", "Bearer " + user.getDigitalOceanToken());
		return request;
	}

	private User getUserFromRequest() {
		String authToken = request().getHeader(AuthenticationController.AUTH_TOKEN);
		User user = userService.findByAuthToken(authToken);
		return user;
	}

}
