package controllers.communication;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.ec2.model.*;
import controllers.security.Secured;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import play.mvc.Security;

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
        String keyName = getUserFromRequest().getAwsKeypairName();
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


	@Security.Authenticated(Secured.class)
	public Promise<Result> listAvailableImages() {
		JsonNode json = createListImagesResponseJson();
		return wrapResultAsPromise(ok(json));
	}

	@Security.Authenticated(Secured.class)
	public Promise<Result> listInstanceTypes() {
		JsonNode json = createListInstanceTypesResponseJson(InstanceType.values());
		return wrapResultAsPromise(ok(json));
	}

	@Security.Authenticated(Secured.class)
	public Promise<Result> listKeyNames() {
		AmazonEC2Client amazonEC2Client = createEC2Client();
		JsonNode json = createListKeyNamesResponseJson(amazonEC2Client.describeKeyPairs().getKeyPairs());
		return wrapResultAsPromise(ok(json));
	}

	@Security.Authenticated(Secured.class)
	public Promise<Result> listSecurityGroups() {
		AmazonEC2Client amazonEC2Client = createEC2Client();
		JsonNode json = createListSecurityGroupsResponseJson(amazonEC2Client.describeSecurityGroups().getSecurityGroups());
		return wrapResultAsPromise(ok(json));
	}

	private JsonNode createListInstanceTypesResponseJson(InstanceType[] values) {
		List<String> types = new ArrayList<String>();
		for(InstanceType type : values) {
			types.add(type.toString());
		}
		return Json.toJson(types);
	}

	private JsonNode createListSecurityGroupsResponseJson(List<SecurityGroup> securityGroups) {
		List<String> groupNames = new ArrayList<String>();
		for(SecurityGroup securityGroup : securityGroups){
			groupNames.add(securityGroup.getGroupName());
		}
		return Json.toJson(groupNames);
	}

	private JsonNode createListKeyNamesResponseJson(List<KeyPairInfo> keyPairs) {
		List<String> keyNames = new ArrayList<String>();
		for(KeyPairInfo keyPairInfo : keyPairs) {
			keyNames.add(keyPairInfo.getKeyName());
		}
		return Json.toJson(keyNames);
	}

	private JsonNode createListImagesResponseJson() {

		List<AwsImage> awsImages = new ArrayList<AwsImage>();
		awsImages.add(new AwsImage("Amazon Linux AMI 2015.03 (HVM) x64","ami-e7527ed7"));
		awsImages.add(new AwsImage("Red Hat Enterprise Linux 7.1 (HVM) x64","ami-4dbf9e7d"));
		awsImages.add(new AwsImage("SUSE Linux Enterprise Server 12 (HVM) x64","ami-d7450be7"));
		awsImages.add(new AwsImage("Ubuntu Server 14.04 LTS (HVM) x64","ami-5189a661"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2012 R2 Base x64","ami-8fd3f9bf"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2012 R2 with SQL Server Express x64","ami-95d3f9a5"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2012 R2 with SQL Server Web x64","ami-53d3f963"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2012 R2 with SQL Server Standard x64","ami-b1d3f981"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2012 Base x64","ami-63d3f953"));
		awsImages.add(new AwsImage("Ubuntu Server 14.04 LTS (PV) x64","ami-6989a659"));
		awsImages.add(new AwsImage("SUSE Linux Enterprise Server 11 SP3 (PV) x64","ami-5df2ab6d"));
		awsImages.add(new AwsImage("Amazon Linux AMI 2015.03 (PV) x64","ami-ff527ecf"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2003 R2 Base x32","ami-a3c9e393"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2003 R2 Base x64","ami-51c9e361"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2008 Base x32","ami-11cee421"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2008 Base x64","ami-43c2e873"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2008 R2 with SQL Server Express and IIS x64","ami-0dcee43d"));
		awsImages.add(new AwsImage("Microsoft Windows Server 2008 R2 Base x64","ami-9dc9e3ad"));
		return Json.toJson(awsImages);
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
