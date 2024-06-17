/*
 * Copyright (c) 2024 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadcom.tanzu.demos.springai101.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
class ChatV4Controller {
    private final ChatClient chatClient;

    @Value("classpath:/simple-rag.st")
    private Resource userText;

    @Value("classpath:/custom-data.md")
    private Resource docsToStuffResource;

    ChatV4Controller(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping(value = "/chat/v4")
    String chat(@RequestParam("q") String query) throws IOException {
        // Add custom context to our query
        String context = docsToStuffResource.getContentAsString(StandardCharsets.UTF_8);
        return chatClient.prompt()
                .user(p -> p.text(userText).params(Map.of("context", context, "question", query)))
                .call()
                .content();
    }
}
