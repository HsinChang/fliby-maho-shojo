package org.example.mirai.plugin;

import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.lang.reflect.Member;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DailyHash extends SimpleListenerHost {
    private ListeningStatus onEvent(GroupMessageEvent event) throws Exception {
        String s = event.getMessage().contentToString();
        if (s.contains("今日人品")){
            MessageDigest digest = MessageDigest.getInstance("MD5");
            MessageChain msg = new At(event.getSender().getId()).plus("今天的人品是").plus("0");
            event.getGroup().sendMessage(msg);
        } else if (s.contains("今日老婆")){
            MessageDigest digest = MessageDigest.getInstance("MD5");
            MessageChain msg = new At(event.getSender().getId()).plus("今天的老婆是").plus("0");
            event.getGroup().sendMessage(msg);
        }
        return ListeningStatus.LISTENING;
    }
}
