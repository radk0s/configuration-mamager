package controllers.communication;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TimeZone;

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
import utils.Hasher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import controllers.security.AuthenticationController;

public class AwsCommunicationModule extends Controller implements ProviderCommunicationModule {
	@Inject
	private UserPersistenceService userService;
	/**
	 * From http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
	 * 
	 * The query API that many AWS services support lets you make requests using either HTTP GET or POST. (In the query
	 * API, you can use GET even if you're making requests that change state; that is, the query API is not inherently
	 * RESTful.) Because GET requests pass parameters on the query string, they are limited to the maximum length of a
	 * URL. Therefore, if a request includes a large payload—for example, if you are uploading a large IAM policy or
	 * parameters in JSON format for a DynamoDB request—you generally use a POST request. The signing process is the
	 * same for both types of requests, although there are slight differences if you're making a GET request and you're
	 * also including the authentication information in the query string.
	 *
	 */
	private String service = "ec2";
	private String host = Urls.AWS_HOST.toString();
	private String region = "us-east-1";
	private String endpoint = Urls.AWS_URL.toString();

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
	 * Documentation: http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_CreateImage.html
	 * 
	 * Example: https://ec2.amazonaws.com/?Action=CreateImage &Description=Standard+Web+Server+v1.0
	 * &InstanceId=i-10a64379 &Name=standard-web-server-v1.0 &AUTHPARAMS
	 * 
	 * Our API - without Action parameter and AUTHPARAMS
	 */
	@Override
	public Promise<Result> createInstance() throws Exception {
		JsonNode json = createJson("CreateImage", request().body().asJson());
		WSRequestHolder request = createRequest("POST", json.asText(), "application/x-amz-json-1.0");
		return request.post(request().body().asJson()).map(function);
	}

	/**
	 * Documentation: http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_RunInstances.html
	 * 
	 * Example: https://ec2.amazonaws.com/?Action=RunInstances &ImageId=ami-60a54009 &MaxCount=3 &MinCount=1
	 * &KeyName=my-key-pair &Placement.AvailabilityZone=us-east-1d &AUTHPARAMS
	 * 
	 * Our API - without Action parameter and AUTHPARAMS
	 */
	@Override
	public Promise<Result> runInstance() throws Exception {
		JsonNode json = createJson("RunInstances", request().body().asJson());
		WSRequestHolder request = createRequest("POST", json.asText(), "application/x-amz-json-1.0");
		return request.post(request().body().asJson()).map(function);
	}

	private JsonNode createJson(String actionName, JsonNode requestJson) {
		ObjectNode json = Json.newObject();
		json.put("Action", actionName);
		while (requestJson.fields().hasNext()) {
			Entry<String, JsonNode> field = requestJson.fields().next();
			json.put(field.getKey(), field.getValue());
		}
		json.put("Version", "2015-03-01");
		return json;
	}

	/**
	 * Documentation:http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_StopInstances.html
	 * 
	 * Example:https://ec2.amazonaws.com/?Action=StopInstances &InstanceId.1=i-10a64379 &AUTHPARAMS
	 * 
	 * Our API - without Action parameter and AUTHPARAMS
	 */
	@Override
	public Promise<Result> stopInstance() throws Exception {
		JsonNode json = createJson("StopInstances", request().body().asJson());
		WSRequestHolder request = createRequest("POST", json.asText(), "application/x-amz-json-1.0");
		return request.post(request().body().asJson()).map(function);
	}

	/**
	 * Documentation: http://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DeregisterImage.html
	 * 
	 * Example:https://ec2.amazonaws.com/?Action=DeregisterImage &ImageId=ami-4fa54026 &AUTHPARAMS
	 * 
	 * Our API - without Action parameter and AUTHPARAMS
	 */
	@Override
	public Promise<Result> deleteInstance() throws Exception {
		JsonNode json = createJson("DeregisterImage", request().body().asJson());
		WSRequestHolder request = createRequest("POST", json.asText(), "application/x-amz-json-1.0");
		return request.post(request().body().asJson()).map(function);
	}

	@Override
	public Promise<Result> listInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	String sign(String key, String msg) throws Exception {
		return Hasher.calculateHmacMd5(key, Charset.forName("UTF-8").encode(msg).toString());
	}

	String getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
		String kDate = sign(Charset.forName("UTF-8").encode("AWS4" + key).toString(), dateStamp);
		String kRegion = sign(kDate, regionName);
		String kService = sign(kRegion, serviceName);
		String kSigning = sign(kService, "aws4_request");
		return kSigning;
	}

	private String timestamp(Date time) {
		String timestamp = null;

		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		timestamp = dfm.format(time);
		return timestamp;
	}

	// AWS Version 4 signing example

	// EC2 API (DescribeRegions)

	// See: http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
	// This version makes a GET/POST request and passes the signature
	// in the Authorization header.
	public WSRequestHolder createRequest(String method, String requestParameters, String contentType) throws Exception {

		// ************* REQUEST VALUES *************
		// String method = "GET";

		// String requestParameters = "Action=DescribeRegions&Version=2013-10-15";

		// Key derivation functions. See:
		// http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html//signature-v4-examples-python

		// Read AWS access key from env. variables or configuration file. Best practice is NOT
		// to embed credentials in code.
		User user = getUserFromRequest();
		String accessKey = user.getEmail();
		String secretKey = user.getAwsToken();
		// if access_key is None or secret_key is None:
		// print "No access key is available."
		// sys.exit()

		// Create a date for headers and the credential string
		Calendar cal = Calendar.getInstance();
		Date t = cal.getTime();
		String amzdate = timestamp(t);
		DateFormat format = new SimpleDateFormat("%Y%m%d");
		String datestamp = format.format(t); // Date w/o time, used in credential scope

		// ************* TASK 1: CREATE A CANONICAL REQUEST *************
		// http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html

		// Step 1 is to define the verb (GET, POST, etc.)--already done.

		// Step 2: Create canonical URI--the part of the URI from domain to query
		// string (use "/" if no path)
		String canonicalUri = "/";

		// Step 4: Create the canonical headers and signed headers. Header names
		// and value must be trimmed and lowercase, and sorted in ASCII order.
		// Note that there is a trailing \n.
		String canonicalHeaders = "host:" + host + "\n" + "x-amz-date:" + amzdate + "\n";

		// Step 5: Create the list of signed headers. This lists the headers
		// in the canonical_headers list, delimited with ";" and in alpha order.
		// Note: The request can include any headers; canonical_headers and
		// signed_headers lists those that you want to be included in the
		// hash of the request. "Host" and "x-amz-date" are always required.
		String signedHeaders = "host;x-amz-date";
		String canonicalQuerystring = null;
		String payloadHash = null;
		if (method.equals("GET")) {
			// Step 3: Create the canonical query string. In this example (a GET request),
			// request parameters are in the query string. Query string values must
			// be URL-encoded (space=%20). The parameters must be sorted by name.

			canonicalQuerystring = requestParameters;
			// Step 6: Create payload hash (hash of the request body content). For GET
			// requests, the payload is an empty string ("").
			payloadHash = Hasher.calculateSha256("");

		} else {
			canonicalHeaders = "content-type:" + contentType + "\n" + canonicalHeaders;
			signedHeaders = contentType + ";" + signedHeaders;
			// Step 3: Create the canonical query string. In this example, request
			// parameters are passed in the body of the request and the query string
			// is blank.
			canonicalQuerystring = "";
			// Step 6: Create payload hash. In this example, the payload (body of
			// the request) contains the request parameters.
			payloadHash = Hasher.calculateSha256(requestParameters);
		}
		// Step 7: Combine elements to create create canonical request
		String canonicalRequest = method + "\n" + canonicalUri + "\n" + canonicalQuerystring + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;

		// ************* TASK 2: CREATE THE STRING TO SIGN*************
		// Match the algorithm to the hashing algorithm you use, either SHA-1 or
		// SHA-256 (recommended)
		String algorithm = "AWS4-HMAC-SHA256";
		String credentialScope = datestamp + "/" + region + "/" + service + "/" + "aws4_request";
		String stringToSign = algorithm + "\n" + amzdate + "\n" + credentialScope + "\n" + Hasher.calculateSha256(canonicalRequest);

		// ************* TASK 3: CALCULATE THE SIGNATURE *************
		// Create the signing key using the function defined above.
		String signingKey = getSignatureKey(secretKey, datestamp, region, service);

		// Sign the string_to_sign using the signing_key
		String signature = Hasher.calculateHexHmacMd5(signingKey, Charset.forName("UTF-8").encode(stringToSign).toString());

		// ************* TASK 4: ADD SIGNING INFORMATION TO THE REQUEST *************
		// The signing information can be either in a query string value or in
		// a header named Authorization. This code shows how to use a header.
		// Create authorization header and add to request headers
		String authorizationHeader = algorithm + " " + "Credential=" + accessKey + "/" + credentialScope + ", " + "SignedHeaders=" + signedHeaders + ", " + "Signature="
				+ signature;

		// ************* CREATE THE REQUEST *************
		String request_url = endpoint + "?" + canonicalQuerystring;
		WSRequestHolder request = WS.url(request_url);
		// The request can include any headers, but MUST include "host", "x-amz-date",
		// and (for this scenario) "Authorization". "host" and "x-amz-date" must
		// be included in the canonical_headers and signed_headers, as noted
		// earlier. Order here is not significant.

		if (method.equals("POST"))
			request.setHeader("Content-Type", contentType);
		request.setHeader("host", host);
		request.setHeader("x-amz-date", amzdate);
		request.setHeader("Authorization", authorizationHeader);
		return request;
	}

	private User getUserFromRequest() {
		String authToken = request().getHeader(AuthenticationController.AUTH_TOKEN);
		User user = userService.findByAuthToken(authToken);
		return user;
	}
}
