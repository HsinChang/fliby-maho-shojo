package org.example.mirai.plugin;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 使用 Java 请把
 * {@code /src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin}
 * 文件内容改成 {@code org.example.mirai.plugin.JavaPluginMain} <br/>
 * 也就是当前主类全类名
 *
 * 使用 Java 可以把 kotlin 源集删除且不会对项目有影响
 *
 * 在 {@code settings.gradle.kts} 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 {@link JvmPluginDescription} 修改插件名称，id 和版本等
 *
 * 可以使用 {@code src/test/kotlin/RunMirai.kt} 在 IDE 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();
    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("com.fliby.mahoshojo", "0.1.0")
                .name("Fliby Maho Shojo")
                .info("キラキラ✨くるくる～")
                .author("ポッチャマ")
                .build());
    }
/**
 *计算人品的哈希函数
 *qq就是QQ号，Mirai监听到的是long
 *range就是期望这个每日哈希随机数的上限
 */
    private int getDailyHash(long qq, int range) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String ts = qq + new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        byte[] encodeHash = digest.digest(ts.getBytes());
        char[] hexTempHash = new char[2*encodeHash.length];
        for (int j = 0; j < encodeHash.length; j++) {
            int v = encodeHash[j] & 0xFF;
            hexTempHash[j * 2] =  hexArray[v >>> 4];
            hexTempHash[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String hexHash = new String(hexTempHash).replaceAll("\\D+", "").substring(0,5);
        Integer intHash = Integer.parseInt(hexHash);
        return intHash%range;
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");
        EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(this);
//        eventChannel.registerListenerHost(new DailyHash());
        eventChannel.subscribeAlways(GroupMessageEvent.class, g -> {
            //监听群消息
//            getLogger().info(g.getMessage().contentToString());
            String s = g.getMessage().contentToString();
            if (s.equals("今日人品")){
                MessageChain msg = null;
                try {
                    msg = new At(g.getSender().getId()).plus("今天的人品是").plus(Integer.toString(getDailyHash(g.getSender().getId(),101))).plus("!");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                g.getGroup().sendMessage(msg);
            } else if (s.equals("今日老婆")){
                MessageChain msg = new At(g.getSender().getId()).plus("今天的老婆是").plus("0").plus("!");
                g.getGroup().sendMessage(msg);
            }
        });
        eventChannel.subscribeAlways(FriendMessageEvent.class, f -> {
            //监听好友消息
            getLogger().info(f.getMessage().contentToString());
        });
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
}
