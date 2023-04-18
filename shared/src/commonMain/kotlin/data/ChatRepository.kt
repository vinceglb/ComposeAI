package data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class ChatRepository(private val openAI: OpenAI) {

    @OptIn(BetaOpenAI::class)
    suspend fun sendMessage(messages: List<ChatMessage>): ChatCompletion {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages,
        )
        return openAI.chatCompletion(request)
    }

}
