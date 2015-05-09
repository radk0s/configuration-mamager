package controllers;

import persistence.model.User;
import persistence.services.UserPersistenceService;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import com.google.inject.Inject;

import controllers.security.AuthenticationController;
import controllers.security.Secured;

public class Application extends Controller {

	@Inject
	private UserPersistenceService userService;

	@Authenticated(Secured.class)
	public static Result index() {
		return ok();
	}

	@Authenticated(Secured.class)
	public Result hello() {
		response().setContentType("application/json");
		User user = null;
		String authToken = ctx().request().headers().get(AuthenticationController.AUTH_TOKEN)[0];
		if ((authToken != null)) {
			user = userService.findByAuthToken(authToken);
		}


		final long userToken = user != null ? user.getId() : 1;
		final String userEmail = user != null ? user.getEmail() : "";
		final String userDOToken = user != null ? user.getDigitalOceanToken() : "";
		final String userAWSAccessKey = user != null ? user.getAwsAccessKey() : "";
        final String userAWSSecretKey = user != null ? user.getAwsSecretKey() : "";


        return ok("{\"userToken\":\"" + userToken + "\",\"userEmail\":\"" + userEmail + "\",\"userDOToken\":\""
				+ userDOToken + "\",\"userAWSAccessKey\":\"" + userAWSAccessKey+ "\",\"userAWSSecretKey\":\"" + userAWSSecretKey + "\"}");
	}

	//TODO - Mock method, replace with real implementation
	public Result listInstances() {
		response().setContentType("application/json");
		return ok("{\n" +
				"  \"droplets\": [\n" +
				"    {\n" +
				"      \"id\": 3164444,\n" +
				"      \"name\": \"example.com\",\n" +
				"      \"memory\": 512,\n" +
				"      \"vcpus\": 1,\n" +
				"      \"disk\": 20,\n" +
				"      \"locked\": false,\n" +
				"      \"status\": \"active\",\n" +
				"      \"kernel\": {\n" +
				"        \"id\": 2233,\n" +
				"        \"name\": \"Ubuntu 14.04 x64 vmlinuz-3.13.0-37-generic\",\n" +
				"        \"version\": \"3.13.0-37-generic\"\n" +
				"      },\n" +
				"      \"created_at\": \"2014-11-14T16:29:21Z\",\n" +
				"      \"features\": [\n" +
				"        \"backups\",\n" +
				"        \"ipv6\",\n" +
				"        \"virtio\"\n" +
				"      ],\n" +
				"      \"backup_ids\": [\n" +
				"        7938002\n" +
				"      ],\n" +
				"      \"snapshot_ids\": [\n" +
				"\n" +
				"      ],\n" +
				"      \"image\": {\n" +
				"        \"id\": 6918990,\n" +
				"        \"name\": \"14.04 x64\",\n" +
				"        \"distribution\": \"Ubuntu\",\n" +
				"        \"slug\": \"ubuntu-14-04-x64\",\n" +
				"        \"public\": true,\n" +
				"        \"regions\": [\n" +
				"          \"nyc1\",\n" +
				"          \"ams1\",\n" +
				"          \"sfo1\",\n" +
				"          \"nyc2\",\n" +
				"          \"ams2\",\n" +
				"          \"sgp1\",\n" +
				"          \"lon1\",\n" +
				"          \"nyc3\",\n" +
				"          \"ams3\",\n" +
				"          \"nyc3\"\n" +
				"        ],\n" +
				"        \"created_at\": \"2014-10-17T20:24:33Z\",\n" +
				"        \"type\": \"snapshot\",\n" +
				"        \"min_disk_size\": 20\n" +
				"      },\n" +
				"      \"size\": {\n" +
				"      },\n" +
				"      \"size_slug\": \"512mb\",\n" +
				"      \"networks\": {\n" +
				"        \"v4\": [\n" +
				"          {\n" +
				"            \"ip_address\": \"104.236.32.182\",\n" +
				"            \"netmask\": \"255.255.192.0\",\n" +
				"            \"gateway\": \"104.236.0.1\",\n" +
				"            \"type\": \"public\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"v6\": [\n" +
				"          {\n" +
				"            \"ip_address\": \"2604:A880:0800:0010:0000:0000:02DD:4001\",\n" +
				"            \"netmask\": 64,\n" +
				"            \"gateway\": \"2604:A880:0800:0010:0000:0000:0000:0001\",\n" +
				"            \"type\": \"public\"\n" +
				"          }\n" +
				"        ]\n" +
				"      },\n" +
				"      \"region\": {\n" +
				"        \"name\": \"New York 3\",\n" +
				"        \"slug\": \"nyc3\",\n" +
				"        \"sizes\": [\n" +
				"\n" +
				"        ],\n" +
				"        \"features\": [\n" +
				"          \"virtio\",\n" +
				"          \"private_networking\",\n" +
				"          \"backups\",\n" +
				"          \"ipv6\",\n" +
				"          \"metadata\"\n" +
				"        ],\n" +
				"        \"available\": null\n" +
				"      }\n" +
				"    },\n" +
				"    {\n" +
				"      \"id\": 3164444,\n" +
				"      \"name\": \"example.com\",\n" +
				"      \"memory\": 512,\n" +
				"      \"vcpus\": 1,\n" +
				"      \"disk\": 20,\n" +
				"      \"locked\": false,\n" +
				"      \"status\": \"active\",\n" +
				"      \"kernel\": {\n" +
				"        \"id\": 2233,\n" +
				"        \"name\": \"Ubuntu 14.04 x64 vmlinuz-3.13.0-37-generic\",\n" +
				"        \"version\": \"3.13.0-37-generic\"\n" +
				"      },\n" +
				"      \"created_at\": \"2014-11-14T16:29:21Z\",\n" +
				"      \"features\": [\n" +
				"        \"backups\",\n" +
				"        \"ipv6\",\n" +
				"        \"virtio\"\n" +
				"      ],\n" +
				"      \"backup_ids\": [\n" +
				"        7938002\n" +
				"      ],\n" +
				"      \"snapshot_ids\": [\n" +
				"\n" +
				"      ],\n" +
				"      \"image\": {\n" +
				"        \"id\": 6918990,\n" +
				"        \"name\": \"14.04 x64\",\n" +
				"        \"distribution\": \"Ubuntu\",\n" +
				"        \"slug\": \"ubuntu-14-04-x64\",\n" +
				"        \"public\": true,\n" +
				"        \"regions\": [\n" +
				"          \"nyc1\",\n" +
				"          \"ams1\",\n" +
				"          \"sfo1\",\n" +
				"          \"nyc2\",\n" +
				"          \"ams2\",\n" +
				"          \"sgp1\",\n" +
				"          \"lon1\",\n" +
				"          \"nyc3\",\n" +
				"          \"ams3\",\n" +
				"          \"nyc3\"\n" +
				"        ],\n" +
				"        \"created_at\": \"2014-10-17T20:24:33Z\",\n" +
				"        \"type\": \"snapshot\",\n" +
				"        \"min_disk_size\": 20\n" +
				"      },\n" +
				"      \"size\": {\n" +
				"      },\n" +
				"      \"size_slug\": \"512mb\",\n" +
				"      \"networks\": {\n" +
				"        \"v4\": [\n" +
				"          {\n" +
				"            \"ip_address\": \"104.236.32.182\",\n" +
				"            \"netmask\": \"255.255.192.0\",\n" +
				"            \"gateway\": \"104.236.0.1\",\n" +
				"            \"type\": \"public\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"v6\": [\n" +
				"          {\n" +
				"            \"ip_address\": \"2604:A880:0800:0010:0000:0000:02DD:4001\",\n" +
				"            \"netmask\": 64,\n" +
				"            \"gateway\": \"2604:A880:0800:0010:0000:0000:0000:0001\",\n" +
				"            \"type\": \"public\"\n" +
				"          }\n" +
				"        ]\n" +
				"      },\n" +
				"      \"region\": {\n" +
				"        \"name\": \"New York 3\",\n" +
				"        \"slug\": \"nyc3\",\n" +
				"        \"sizes\": [\n" +
				"\n" +
				"        ],\n" +
				"        \"features\": [\n" +
				"          \"virtio\",\n" +
				"          \"private_networking\",\n" +
				"          \"backups\",\n" +
				"          \"ipv6\",\n" +
				"          \"metadata\"\n" +
				"        ],\n" +
				"        \"available\": null\n" +
				"      }\n" +
				"    }\n" +
				"  ],\n" +
				"  \"links\": {\n" +
				"    \"pages\": {\n" +
				"      \"last\": \"https://api.digitalocean.com/v2/droplets?page=3&per_page=1\",\n" +
				"      \"next\": \"https://api.digitalocean.com/v2/droplets?page=2&per_page=1\"\n" +
				"    }\n" +
				"  },\n" +
				"  \"meta\": {\n" +
				"    \"total\": 3\n" +
				"  }\n" +
				"}");
	}

}
