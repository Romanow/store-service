import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Component
@Profile({ "integration-test", "romanow", "ift", "psi", "prod" })
public class SecurityRoleBasedFilter
    implements WebFilter {

    private final AclibPermissionService aclibPermissionService;
    private final Map<OpenApiConfig, Map<MethodInfo, String>> securityMap;

    @SuppressWarnings("unchecked")
    public SecurityRoleBasedFilter(
        @NotNull AclibPermissionService aclibPermissionService,
        @NotNull ApplicationProperties applicationProperties,
        @NotNull Map<String, OpenAPI> apiMap
    ) {
        this.aclibPermissionService = aclibPermissionService;
        this.securityMap = new HashMap<>();
        for (var entry : apiMap.entrySet()) {
            final var serviceName = entry.getKey();
            final var api = entry.getValue();

            final var paths = new HashMap<MethodInfo, String>();
            for (var pathItem : api.getPaths().entrySet()) {
                var path = pathItem.getKey();
                for (var operationEntry : pathItem.getValue().readOperationsMap().entrySet()) {
                    final var method = HttpMethod.valueOf(operationEntry.getKey().name());
                    final var operation = operationEntry.getValue();
                    if (operation.getExtensions() != null) {
                        final var security = operation.getExtensions().get(SECURITY_EXTENSION_NAME);
                        if (security != null) {
                            if (!(security instanceof LinkedHashMap)) {
                                throw new IllegalArgumentException(format("'%s' not instance of Map", security));
                            }
                            final var map = (LinkedHashMap<String, String>) security;
                            final var permission = map.get(PERMISSION_KEY);
                            final var parameters = operation.getParameters();
                            final Map<String, PathParam> pathParams =
                                parameters != null ? parameters.stream().collect(toMap(Parameter::getName, p -> {
                                    final var schema = p.getSchema();
                                    final var pattern =
                                        schema.getPattern() != null ? compile(schema.getPattern()) : null;
                                    return new PathParam(schema.getType(), schema.getFormat(), pattern);
                                })) : Map.of();
                            final var methodInfo = new MethodInfo(method, path, pathParams);
                            paths.put(methodInfo, permission);
                        }
                    }
                }
            }
            final var config = applicationProperties.getApis().get(serviceName);
            securityMap.put(config, paths);
        }
    }

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        final var request = exchange.getRequest();
        final var requestMethod = request.getMethod();
        final var requestPath = request.getPath();

        var permissions = securityMap
            .entrySet()
            .stream()
            .flatMap(entry -> entry.getKey()
                .getPatterns()
                .stream()
                .map(pathPattern -> Pair.of(pathPattern, entry.getValue())))
            .filter(entry -> new AntPathMatcher().match(entry.getKey().getPath(),
                requestPath.value()))
            .flatMap(entry -> {
                final var path = trimSlash(requestPath, entry.getKey().getStripPath());
                return entry.getValue().entrySet().stream().filter(k -> {
                    final var methodInfo = k.getKey();
                    final var matcher = new AntPathMatcher();
                    if (methodInfo.method().equals(requestMethod) && matcher.match(methodInfo.path, path.value())) {
                        final var params = matcher.extractUriTemplateVariables(methodInfo.path, path.value());
                        var matches = params.isEmpty();
                        for (var param : params.keySet()) {
                            if (methodInfo.pathParams.containsKey(param)) {
                                final var pathValue = params.get(param);
                                final var pathParam = methodInfo.pathParams.get(param);
                                // check type (string, integer, number)
                                // check subtype (int32, int64, uuid)
                                if (pathParam.pattern != null) {
                                    matches = pathParam.pattern.matcher(pathValue).matches();
                                }
                            }
                        }
                        return matches;
                    }
                    return false;
                }).map(Entry::getValue);
            })
            .toList();

        log.info("Permissions '{}' for {} {}", permissions, requestMethod, requestPath);
        return permissions.stream()
            .findFirst()
            .map(s -> just(aclibPermissionService.permittedByRole(new AttributeOptions(s, false)))
                .flatMap(result -> result)
                .filter(result -> result)
                .switchIfEmpty(defer(() -> Mono.error(
                    new AccessDeniedException("No find permission for this operation"))
                ))
                .then(chain.filter(exchange)))
            .orElseGet(() -> chain.filter(exchange));
    }

    private record MethodInfo(HttpMethod method, String path, Map<String, PathParam> pathParams) {

    }

    private record PathParam(String type, String subtype, Pattern pattern) {

    }

}
