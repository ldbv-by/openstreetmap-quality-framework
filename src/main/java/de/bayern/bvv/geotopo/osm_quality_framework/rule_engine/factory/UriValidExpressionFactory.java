package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Validates a tag as either a URN (via regex) or an HTTP(S) URL that returns 200 (redirects supported).
 */
@Component
public class UriValidExpressionFactory implements ExpressionFactory {

    private static final HttpClient HTTP;

    static {
        HttpClient.Builder b = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(3));

        ProxySelector ps = ProxySelector.getDefault();
        if (ps != null) b.proxy(ps);
        HTTP = b.build();
    }

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "uri_valid";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String urnPattern
    ) {}

    /**
     * Defines the rule parameters and the execution block of a rule.
     */
    @Override
    public Expression create(JsonNode json) {

        // ----- Parse rule params ------
        RuleParams params = this.parseParams(json);

        // ----- Execute rule ------
        return (taggedObject, baseTaggedObject) -> {
            String uri = taggedObject.getTags().get(params.tagKey);

            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                return isValidHttpUrl(uri);
            }

            if (!(params.urnPattern.isEmpty())) {
                try {
                    Pattern pattern = Pattern.compile(params.urnPattern);
                    return pattern.matcher(uri).matches();
                } catch (PatternSyntaxException e) {
                    throw new IllegalArgumentException(type() + ": invalid pattern: " + e.getMessage(), e);
                }
            }

            return false;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String urnPattern = JsonUtils.asOptionalString(json, "urn_pattern");

        return new RuleParams(tagKey, urnPattern);
    }

    /**
     * Check if http url is reachable.
     */
    private static boolean isValidHttpUrl(String httpUrl) {
        try {
            URI uri = new URI(httpUrl);

            String scheme = uri.getScheme();
            if (scheme == null) return false;
            String s = scheme.toLowerCase(Locale.ROOT);
            if (!Objects.equals(s, "http") && !Objects.equals(s, "https")) return false;

            if (uri.getHost() == null) return false;

            HttpRequest req = HttpRequest.newBuilder(uri)
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .header("User-Agent", "OSM-Quality-Checker/1.0")
                    .build();

            HttpResponse<Void> resp = HTTP.send(req, HttpResponse.BodyHandlers.discarding());
            return resp.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
