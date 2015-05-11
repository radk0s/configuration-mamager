package controllers.communication;

import com.google.inject.Inject;
import controllers.security.Secured;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import java.util.ArrayList;
import java.util.List;

import static controllers.communication.AwsCommunicationModule.wrapResultAsPromise;
import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Created by Ewelina on 2015-05-11.
 */
public class CommonCommunicationModule {

    @Inject
    DigitalOceanCommunicationModule digitalOceanCommunicationModule;

    @Inject
    AwsCommunicationModule awsCommunicationModule;

    @Authenticated(Secured.class)
    public Promise<Result> listAllInstances() {
        final Promise<Result> doResult = digitalOceanCommunicationModule.listInstances();
        final Promise<Result> awsResult = awsCommunicationModule.listInstances();

        return doResult.flatMap(new F.Function<Result, Promise<Result>>() {
            @Override
            public Promise<Result> apply(final Result doResult) throws Throwable {
                return awsResult.flatMap(new F.Function<Result, Promise<Result>>() {
                    @Override
                    public Promise<Result> apply(Result awsResult) throws Throwable {
                        List<Result> promises = new ArrayList<Result>();
                        promises.add(doResult);
                        promises.add(awsResult);
                        return wrapResultAsPromise(ok(toJson(promises)));
                    }
                });
            }
        });
    }
}
