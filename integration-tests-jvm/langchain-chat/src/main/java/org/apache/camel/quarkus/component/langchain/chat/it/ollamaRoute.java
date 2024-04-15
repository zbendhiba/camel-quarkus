package org.apache.camel.quarkus.component.langchain.chat.it;

import org.apache.camel.builder.RouteBuilder;

public class ollamaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
            from("direct:send-simple-message")
                .to("langchain-chat:test1?chatModel={{chatModel}}&chatOperation=CHAT_SINGLE_MESSAGE")
                .to("mock:response");


            from("direct:send-message-prompt")
                .to("langchain-chat:test2?chatModel=#chatModel&chatOperation=CHAT_SINGLE_MESSAGE_WITH_PROMPT")
                .to("mock:response");

            from("direct:send-multiple")
                .to("langchain-chat:test2?chatModel=#chatModel&chatOperation=CHAT_MULTIPLE_MESSAGES")
                .to("mock:response");

    }
}