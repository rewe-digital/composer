package com.rewedigital.composer.composing;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Client;

/**
 * <code>TemplateComposer</code> provides the interface to execute the main
 * purpose of <em>Composer</em>: Composing a web page out of a template and
 * content fragments.
 *
 */
public interface TemplateComposer {

    interface Factory {
        TemplateComposer build(Client client, String path, Map<String, Object> parsedPathArguments,
                ComposingResponse<String> composingResponse);
    }

    CompletableFuture<ComposingResponse<String>> composeTemplate();
}
