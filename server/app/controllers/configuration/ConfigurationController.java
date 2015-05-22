package controllers.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import controllers.security.AuthenticationController;
import controllers.security.Secured;
import persistence.model.User;
import persistence.services.ConfigurationPersistenceService;
import persistence.services.UserPersistenceService;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

public class ConfigurationController extends Controller {

    @Inject
    UserPersistenceService userService;

    @Inject
    ConfigurationPersistenceService configurationService;

    @Authenticated(Secured.class)
    public Result createConfiguration() {

        final Form<Configuration> configurationForm = Form.form(Configuration.class).bindFromRequest();

        if(configurationForm.hasErrors()) {
            return badRequest(configurationForm.errorsAsJson());
        }

        final Configuration configuration = configurationForm.get();

        if( configurationService.findConfigurationByName(configuration.name) != null ) {
            return badRequest("Configuration " + configuration.name + " already exists!");
        }

        persistence.model.Configuration config = ConfigurationTranslator.convert(configuration);
        final User user = getUserFromRequest();
        config.setUser(user);

        configurationService.save(config);

        return ok(String.valueOf(config.getId()));
    }

    @Authenticated(Secured.class)
    public Result getConfigurations() {
        final User user = getUserFromRequest();
        return ok(Json.toJson(configurationService.getConfigurationsByUser(user)));
    }

    @Authenticated(Secured.class)
    public Result deleteConfiguration() {
        final JsonNode json = request().body().asJson();
        final String configurationName = json.get("name").asText();

        if(configurationName == null || configurationName == "") {
            return badRequest("Invalid or empty configuration name to delete");
        }

        configurationService.deleteByName(configurationName);

        return ok();
    }

    private User getUserFromRequest() {
        String authToken = request().getHeader(AuthenticationController.AUTH_TOKEN);
        User user = userService.findByAuthToken(authToken);
        return user;
    }

    public static class Configuration {
        @Constraints.Required
        public String name;

        @Constraints.Required
        public String data;

        @Constraints.Required
        public String provider;

        /**
         * Validate the configuration.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(name)) {
                return "Configuration name is required";
            }

            if (isBlank(data)) {
                return "Configuration data is required";
            }

            if (isBlank(provider)) {
                return "Configuration provider is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }

    }
}
