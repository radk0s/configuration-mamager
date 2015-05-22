package controllers.communication;

import java.util.List;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import controllers.security.AuthenticationController;

public class AwsCommunicationModule extends Controller implements ProviderCommunicationModule {
	@Inject
	private UserPersistenceService userService;

	public static F.Promise<Result> wrapResultAsPromise(final Result result) {
		return F.Promise.promise(new F.Function0<Result>() {
			public Result apply() {
				return result;
			}
		});
	}

	/**
	 * REQUEST BODY example: { "imageId":"i-10a64379", "instanceType":"t2.micro", "keyName": "keyName", "securityGroup": "securityGroup"}
	 **/

	@Override
	public Promise<Result> createInstance() {
        JsonNode json = request().body().asJson();
        AmazonEC2Client amazonEC2Client = createEC2Client();

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        String imageId = json.get("imageId").asText();
        String instanceType = json.get("instanceType").asText();
        String keyName = json.get("keyName").asText();
        String securityGroup = json.get("securityGroup").asText();

        runInstancesRequest.withImageId(imageId)
                .withInstanceType(instanceType)
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(keyName)
                .withSecurityGroups("default");

        RunInstancesResult runInstancesResult = amazonEC2Client.runInstances(runInstancesRequest);
        JsonNode responseJson = Json.toJson(runInstancesResult.getReservation());

        return wrapResultAsPromise(ok(responseJson));
	}

	/**
	 * REQUEST BODY example: { "instanceId":"ami-60a54009"}
	 */
	@Override
	public Promise<Result> runInstance() {
        JsonNode json = request().body().asJson();
        AmazonEC2Client amazonEC2Client = createEC2Client();

        StartInstancesRequest startInstancesRequest = new StartInstancesRequest();

        String instanceId = json.get("instanceId").asText();
        startInstancesRequest.withInstanceIds(Lists.newArrayList(instanceId));

        StartInstancesResult startInstancesResult = amazonEC2Client.startInstances(startInstancesRequest);
        JsonNode responseJson = Json.toJson(startInstancesResult.getStartingInstances());

        return wrapResultAsPromise(ok(responseJson));
	}

	/**
	 * REQUEST BODY example: { "instanceId":"i-10a64379"}
	 */
	@Override
	public Promise<Result> stopInstance() {
		JsonNode json = request().body().asJson();
		AmazonEC2Client amazonEC2Client = createEC2Client();

		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();

		String instanceId = json.get("instanceId").asText();
		stopInstancesRequest.withInstanceIds(Lists.newArrayList(instanceId));

		StopInstancesResult stopInstancesResult = amazonEC2Client.stopInstances(stopInstancesRequest);
		JsonNode responseJson = Json.toJson(stopInstancesResult.getStoppingInstances());

		return wrapResultAsPromise(ok(responseJson));
	}

	/**
	 * REQUEST BODY example: { "instanceId":"i-10a64379"}
	 */
	@Override
	public Promise<Result> deleteInstance() {
        JsonNode json = request().body().asJson();
        AmazonEC2Client amazonEC2Client = createEC2Client();

        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();

        String instanceId = json.get("instanceId").asText();
        terminateInstancesRequest.withInstanceIds(Lists.newArrayList(instanceId));

        TerminateInstancesResult terminateInstancesResult = amazonEC2Client.terminateInstances(terminateInstancesRequest);
        JsonNode responseJson = Json.toJson(terminateInstancesResult.getTerminatingInstances());

        return wrapResultAsPromise(ok(responseJson));
	}

	@Override
	public Promise<Result> listInstances() {
		AmazonEC2Client amazonEC2Client = createEC2Client();

		JsonNode json = Json.toJson(createListInstancesResponseJson(amazonEC2Client.describeInstances().getReservations()));
		return wrapResultAsPromise(ok(json));
	}

	private ObjectNode createListInstancesResponseJson(List<Reservation> reservations) {
		ObjectNode json = Json.newObject();
		Integer reservationNumber = 1;
		for (Reservation reservation : reservations) {
			json.put(reservationNumber.toString(), Json.toJson(reservation.getInstances()));
            reservationNumber++;
		}
		return json;
	}

	private AmazonEC2Client createEC2Client() {
		AWSCredentials credentials = getCredentials();

		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);
		amazonEC2Client.setEndpoint(Urls.AWS_URL.toString());
		return amazonEC2Client;
	}

	private AWSCredentials getCredentials() {
		User user = getUserFromRequest();
		String keyId = user.getAwsAccessKey();
		String secretKey = user.getAwsSecretKey();
		AWSCredentials credentials = new BasicAWSCredentials(keyId, secretKey);
		return credentials;
	}

	private User getUserFromRequest() {
		String authToken = request().getHeader(AuthenticationController.AUTH_TOKEN);
		User user = userService.findByAuthToken(authToken);
		return user;
	}
}
