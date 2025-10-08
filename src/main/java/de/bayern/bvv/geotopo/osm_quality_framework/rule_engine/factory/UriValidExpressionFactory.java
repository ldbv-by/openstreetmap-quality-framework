package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
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

    @Override
    public String type() {
        return "uri_valid";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String urnPattern = json.path("urn_pattern").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("uri_valid: 'tag_key' is required");
        }

        return feature -> {
            String uri = feature.getTags().get(tagKey);

            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                return isValidHttpUrl(uri);
            }

            if (!(urnPattern == null || urnPattern.isBlank())) {
                try {
                    Pattern pattern = Pattern.compile(urnPattern);
                    return pattern.matcher(uri).matches();
                } catch (PatternSyntaxException e) {
                    throw new IllegalArgumentException("uri_valid: invalid pattern: " + e.getMessage(), e);
                }
            }

            return false;
        };
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
