package com.rewedigital.composer.application;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.composer.composing.CompositionStep;
import com.rewedigital.composer.composing.ContentFetcher;
import com.rewedigital.composer.composing.RequestEnricher;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.StatusType.Family;

import okio.ByteString;

public class ValidatingContentFetcher implements ContentFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatingContentFetcher.class);

    private final Client client;
    private final Map<String, Object> parsedPathArguments;
    private final RequestEnricher requestEnricher;
    private final int maxRecursion;

    public ValidatingContentFetcher(final Client client, final Map<String, Object> parsedPathArguments,
            final RequestEnricher requestEnricher, final int maxRecursion) {
        this.requestEnricher = requireNonNull(requestEnricher);
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
        this.maxRecursion = maxRecursion;
    }

    @Override
    public CompletableFuture<Response<String>> fetch(final String path, final String fallback,
            final Optional<Duration> ttl, final CompositionStep step) {
        if (maxRecursion < step.depth()) {
            LOGGER.warn("Max recursion depth exceeded for " + step.callStack());
            return CompletableFuture.completedFuture(Response.forPayload(fallback));
        }

        if (isEmpty(path)) {
            LOGGER.warn("Empty path attribute in include found - callstack: " + step.callStack());
            return CompletableFuture.completedFuture(Response.forPayload(""));
        }

        final String expandedPath = UriTemplate.fromTemplate(path).expand(parsedPathArguments);
        final Request request = requestEnricher.enrich(withTtl(Request.forUri(expandedPath, "GET"), ttl));

        return client.send(request)
                .thenApply(response -> acceptHtmlOnly(response, expandedPath))
                .thenApply(response -> acceptOkStatusOnly(response, expandedPath))
                .thenApply(r -> toStringPayload(r, fallback))
                .toCompletableFuture();
    }

    private Request withTtl(final Request request, final Optional<Duration> ttl) {
        return ttl.map(t -> request.withTtl(t)).orElse(request);
    }

    private Response<String> toStringPayload(final Response<ByteString> response, final String fallback) {
        final String value = response.payload().map(p -> p.utf8()).orElse(fallback);
        return response.withPayload(value);
    }

    private Response<ByteString> acceptHtmlOnly(final Response<ByteString> response, final String expandedPath) {
        final String contentType = response.header("Content-Type").orElse("other");
        if (contentType != null && contentType.contains("text/html")) {
            return response;
        }
        LOGGER.warn("Content-Type of [{}] is not text/html, returning an empty body.", expandedPath);
        return response.withPayload(null);
    }

    private Response<ByteString> acceptOkStatusOnly(final Response<ByteString> response, final String expandedPath) {
        if (response.status().family().equals(Family.SUCCESSFUL)) {
            return response;
        }
        return response.withPayload(null);
    }

    private static boolean isEmpty(final String string) {
        return string == null || string.trim().isEmpty();
    }
}
