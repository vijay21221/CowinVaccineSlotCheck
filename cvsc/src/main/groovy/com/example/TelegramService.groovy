package com.example

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Property
import io.micronaut.context.event.StartupEvent
import io.micronaut.http.HttpMethod
import io.micronaut.runtime.event.annotation.EventListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.inject.Singleton

@CompileStatic
@Singleton
class TelegramService {
    private static final Log log = LogFactory.getLog(TelegramService.class)

    @Property(name = "telegram.bot.token")
    String BOT_TOKEN

    @Property(name = "telegram.bot.chat-id")
    long chatId

    @Property(name = "telegram.bot.channel")
    String channel

    TelegramBot bot

    @EventListener
    onStartUp(StartupEvent startupEvent){
        bot = new TelegramBot(BOT_TOKEN)
        bot.setUpdatesListener(updates -> {
            // ... process updates
            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        })
    }

    void sendMessage(String message){
        log.info("Sending message : $message to telegram bot!!!")
        ApiRequestInfo apiRequestInfo = new ApiRequestInfo(baseURL: "https://api.telegram.org/",
                endpoint: "https://api.telegram.org/bot$BOT_TOKEN/sendMessage", payload: [text : message, chat_id : "@${channel}"], method: HttpMethod.GET)
        Map response = HttpUtils.apiCaller(apiRequestInfo)
        log.info("Telegram Send message response : ${response.toString()}")
    }
}
