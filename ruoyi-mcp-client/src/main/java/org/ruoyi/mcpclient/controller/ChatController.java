package org.ruoyi.mcpclient.controller;

import io.modelcontextprotocol.client.McpSyncClient;
import org.ruoyi.mcpdemo.controller.domin.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * @author ageer
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory = new InMemoryChatMemory();

    public ChatController(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder
                .defaultOptions(
                        OpenAiChatOptions.builder().model("gpt-4o-mini").build())
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Flux<String> send(@RequestBody ChatRequest request) {
        // 演示环境对话id设置固定值
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory, "ConversationId", 10);
        return this.chatClient.prompt(request.getMessage())
                .advisors(messageChatMemoryAdvisor)
                .stream().content();
    }

}
