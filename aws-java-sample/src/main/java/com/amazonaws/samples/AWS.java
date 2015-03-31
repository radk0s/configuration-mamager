package com.amazonaws.samples;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWS {

    public static void main(String[] args) throws IOException {

        AWSCredentials credentials = new BasicAWSCredentials("keyid", "secretkey");

        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);
        amazonEC2Client.setEndpoint("ec2.eu-central-1.amazonaws.com");

        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        runInstancesRequest.withImageId("ami-a8221fb5")
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("iosr")
                .withSecurityGroups("default");

        RunInstancesResult runInstancesResult =
                amazonEC2Client.runInstances(runInstancesRequest);

    }
}
