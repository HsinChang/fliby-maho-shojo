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
import net.mamoe.mirai.message.data.SingleMessage;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        String hexHash = new String(hexTempHash).replaceAll("\\D+", "").substring(0,8);
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
            if (s.contains("今日人品")){
                long qq = 0;
//                Pattern pattern = Pattern.compile("今日人品\\[mirai:at:\\d+\\]");
//                Matcher matcher = pattern.matcher(s);
                if (s.equals("今日人品")) {
                    qq = g.getSender().getId();
                } else {
                    for (SingleMessage singleMessage : g.getMessage()){
                        if (singleMessage instanceof At){
                            qq = ((At) singleMessage).getTarget();
                        }
                    }
                }
                MessageChain msg = null;
                if(qq!=0) {
                    try {
                        msg = new At(qq).plus("今天的人品是").plus(Integer.toString(getDailyHash(qq, 101))).plus("!");
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    g.getGroup().sendMessage(msg);
                }
            } else if (s.contains("今日老婆")){
                long qq = 0;
//                Pattern pattern = Pattern.compile("今日老婆\\[mirai:at:\\d+\\]");
//                Matcher matcher = pattern.matcher(s);
                if (s.equals("今日老婆")) {
                    qq = g.getSender().getId();
                } else {
                    for (SingleMessage singleMessage : g.getMessage()){
                        if (singleMessage instanceof At){
                            qq = ((At) singleMessage).getTarget();
                        }
                    }
                }
                MessageChain msg = null;
                if(qq!=0) {
                    try {
                        msg = new At(qq).plus("今天的老婆是「").plus(waifus.get(getDailyHash(qq, 374))).plus("」!");
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    g.getGroup().sendMessage(msg);
                }
            }
        });
        eventChannel.subscribeAlways(FriendMessageEvent.class, f -> {
            //监听好友消息
            getLogger().info(f.getMessage().contentToString());
        });
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final static List<String> waifus = Arrays.asList("赤红* レッド Red", "青绿* グリーン Blue", "大木博士 オーキド博士 Professor Oak", "奈奈美* ナナミ Daisy", "小刚 タケシ Brock", "小霞 カスミ Misty", "马志士 マチス Lt. Surge", "莉佳 エリカ Erika", "阿桔 キョウ Koga", "娜姿 ナツメ Sabrina", "夏伯 カツラ Blaine", "坂木 サカキ Giovanni", "科拿 カンナ Lorelei", "希巴 シバ Bruno", "菊子 キクコ Agatha", "阿渡 ワタル Lance", "正辉 マサキ Bill", "武德* タケノリ Koichi", "富士老人 フジろうじん Mr. Fuji", "巴奥巴* バオバ Baoba", "妈妈 ママ Mom", "模仿少女 モノマネむすめ Copycat", "老人 おじいさん Old man", "宝可梦中心的姐姐 ポケモンセンターナース Pokémon Center Nurse", "姓名鉴定师 せいめいはんだんし Name Rater", "武藏 ムサシ Jessie", "小次郎 コジロウ James", "乔伊* ジョーイ Nurse Joy", "君莎* ジュンサー Officer Jenny", "阿响* ヒビキ Ethan", "小银* シルバー Silver", "空木博士 ウツギ博士 Professor Elm", "阿速 ハヤト Falkner", "阿笔 ツクシ Bugsy", "小茜 アカネ Whitney", "松叶 マツバ Morty", "阿四 シジマ Chuck", "阿蜜 ミカン Jasmine", "柳伯 ヤナギ Pryce", "小椿 イブキ Clair", "一树 イツキ Will", "梨花 カリン Karen", "阿杏 アンズ Janine", "阿波罗* アポロ Archer", "雅典娜* アテナ Ariana", "兰斯* ランス Proton", "拉姆达* ラムダ Petrel", "初始 ジョバンニ Earl Dervish", "钢铁 ガンテツ Kurt", "五月 サツキ Miki", "桃桃 スモモ／コモモ Kuni", "玉绪 タマオ Tamao", "小梅 コウメ Naoko", "樱花 サクラ Sayo", "光圣 コウセイ Li", "妈妈 ママ Mom", "宝可梦爷爷 ポケモンおじいさん Mr. Pokémon", "胡桃 クルミ DJ Mary", "莉莉诗 リリス Lily", "黄杨 ツゲ Reed", "洋苏 セージ Ben", "清明 コージ Fern", "光亮 ヒカル／ひかる Cal", "合欢博士 ネムノキ博士 Professor Silktree", "信彦 ノブヒコ Kiyo", "星琪怡 ツキコ Monica", "星琪儿 ヒコ Tuscany", "星琪山 ミズオ Wesley", "星琪思 モクオ Arthur", "星琪舞 カネコ Frieda", "星琪柳 ツチオ Santos", "星琪天 ニチオ Sunny", "遗忘爷爷 わすれオヤジ Move Deleter", "小哥 おにいさん Dude", "克丽丝* クリス Kris", "水京 ミナキ Eusine", "葵妍 アオイ Buena", "龙之长老 ドラゴンつかいいちぞくの长老さま Master of the Dragon Tamer clan", "小悠 ユウキ Brendan", "小遥 ハルカ May", "小田卷博士 オダマキ博士 Professor Birch", "满充 ミツル Wally", "阿满 ミチル Wanda", "杜娟 ツツジ Roxanne", "藤树 トウキ Brawly", "铁旋 テッセン Wattson", "亚莎 アスナ Flannery", "千里 センリ Norman", "娜琪 ナギ Winona", "小枫 フウ Tate", "小南 ラン Liza", "米可利 ミクリ Wallace", "花月 カゲツ Sidney", "芙蓉 フヨウ Phoebe", "波妮 プリム Glacia", "源治 ゲンジ Drake", "大吾 ダイゴ Stone", "库斯诺吉馆长 クスノキ館長 Captain Stern", "都贺 ツガ Dock", "兹伏奇社长 ツワブキ社長 Mr. Stone", "真由美 マユミ Brigette", "赤焰松 マツブサ Maxie", "火村 ホムラ Tabitha", "火雁 カガリ Courtney", "水梧桐 アオギリ Archie", "阿泉 イズミ Shelly", "阿潮 ウシオ Matt", "哈奇老人 ハギ老人 Mr. Briney", "索蓝斯博士 ソライシ博士 Professor Cozmo", "妈妈 ママ Mom", "连胜晴彦 カチヌキ ハルヒコ Victor Winstrate", "连胜安江 カチヌキ ヤスエ Victoria Winstrate", "连胜小雪 カチヌキ アキ Vivi Winstrate", "连胜光代 カチヌキ ミツヨ Vicky Winstrate", "连胜良平 カチヌキ リョウヘイ Vito Winstrate", "玛莉 マリ Gabby", "小戴 ダイ Ty", "风野 カゼノ Rydel", "小桐 キリ Kiri", "招式教学狂 わざおしえマニア Move Reminder", "亚当 アダン Juan", "亚希达 エニシダ Scott", "达拉 ダツラ Noland", "黄瓜香 コゴミ Greta", "希尔斯 ヒース Tucker", "小蓟 アザミ Lucy", "宇康 ウコン Spenser", "神代 ジンダイ Brandon", "莉拉 リラ Anabel", "明辉 コウキ Lucas", "小光 ヒカリ Dawn", "阿驯* ジュン Barry", "山梨博士 ナナカマド博士 Professor Rowan", "彩子 アヤコ Johanna", "瓢太 ヒョウタ Roark", "菜种 ナタネ Gardenia", "阿李 スモモ Maylene", "吉宪 マキシ Crasher Wake", "梅丽莎 メリッサ Fantina", "东瓜 トウガン Byron", "小菘 スズナ Candice", "电次 デンジ Volkner", "阿柳 リョウ Aaron", "菊野 キクノ Bertha", "大叶 オーバ Flint", "悟松 ゴヨウ Lucian", "竹兰 シロナ Cynthia", "芽米 モミ Cheryl", "麦儿 ミル Mira", "亚玄 ゲン Riley", "米依 マイ Marley", "麦可 バク Buck", "赤日 アカギ Cyrus", "伙星 マーズ Mars", "岁星 ジュピター Jupiter", "镇星 サターン Saturn", "桄榔 クロツグ Palmer", "龙婆婆 タツばあさん Wilma", "水木 ミズキ Bebe", "现慕 ウラヤマ Mr. Backlot", "地下大叔 ちかおじさん Underground Man", "多多罗 タタラ Mr. Fuego", "足迹博士 あしあとはかせ Dr. Footstep", "慷慨大叔 ミスター・グッズ Mister Goods", "波木 ナミキ Eldritch", "毕克 ビック Dexter", "蜜蜜 ミミィ Keira", "佐助 サスケ Jordan", "文子 フミコ Julia", "滨名 ハマナ Roseanne", "自行车人力 じてんしゃ じんりき Rad Rickshaw", "琴音 コトネ Lyra", "阿结 ケジメ Maximo", "大拳 コブシ Magnus", "斗也 トウヤ Hilbert", "斗子 トウコ Hilda", "黑连 チェレン Cheren", "白露 ベル Bianca", "红豆杉博士 アララギ博士 Professor Juniper", "红豆杉博士的爸爸 アララギパパ Cedric Juniper", "真菰 マコモ Fennel", "松露 ショウロ Amanita", "天桐 デント Cilan", "伯特 ポッド Chili", "寇恩 コーン Cress", "芦荟 アロエ Lenora", "木立 キダチ Hawes", "亚堤 アーティ Burgh", "小菊儿 カミツレ Elesa", "菊老大 ヤーコン Clay", "风露 フウロ Skyla", "哈奇库 ハチク Brycen", "艾莉丝 アイリス Iris", "夏卡 シャガ Drayden", "婉龙 シキミ Shauntal", "越橘 ギーマ Grimsley", "连武 レンブ Marshal", "阿戴克 アデク Alder", "北尚 ノボリ Ingo", "南厦 クダリ Emmet", "Ｎ Ｎ N", "魁奇思 ゲーチス Ghetsis", "罗德 ロット Rood", "斯姆拉 スムラ Bronius", "杰洛 ジャロ Giallo", "约格斯 リョクシ Ryoku", "阿苏拉 アスラ Gorm", "维奥 ヴィオ Zinzolin", "巴贝娜 バーベナ Concordia", "荷莲娜 ヘレナ Anthea", "黑暗铁三角 ダークトリニティ Shadow Triad", "妈妈 ママ Mom", "森本 モリモト Morimoto", "西野 ニシノ Nishino", "紫檀 シタン Loblolly", "共平 キョウヘイ Nate", "鸣依 メイ Rosa", "阿修 ヒュウ Hugh", "霍米加 ホミカ Roxie", "霍米加的爸爸 ホミカパパ Pop Roxie", "西子伊 シズイ Marlon", "阿克罗玛 アクロマ Colress", "蕃石郎 バンジロウ Benga", "琉璃 ルリ Yancy", "阿铁 テツ Curtis", "卡鲁穆 カルム Calem", "莎莉娜 セレナ Serena", "提耶鲁诺 ティエルノ Tierno", "多罗巴 トロバ Trevor", "莎娜 サナ Shauna", "布拉塔诺博士 プラターヌ博士 Professor Augustine Sycamore", "紫罗兰 ビオラ Viola", "查克洛 ザクロ Grant", "可尔妮 コルニ Korrina", "福爷 フクジ Ramos", "希特隆 シトロン Clemont", "玛绣 マーシュ Valerie", "葛吉花 ゴジカ Olympia", "得抚 ウルップ Wulfric", "志米 ズミ Siebold", "朵拉塞娜 ドラセナ Drasna", "雁铠 ガンピ Wikstrom", "帕琦拉 パキラ Malva", "卡露妮 カルネ Diantha", "库瑟洛斯奇 クセロシキ Xerosic", "茉蜜姬 モミジ  Mable", "芭菈 バラ Bryony", "克蕾儿 コレア Celosia", "阿可碧 アケビ Aliana", "弗拉达利 フラダリ Lysandre", "ＡＺ ＡＺ ＡＺ", "吉娜 ジーナ Sina", "德克希欧 デクシオ Dexio", "三色堇 パンジー Alexa", "萨琪 サキ Grace", "柚丽嘉 ユリーカ Bonnie", "酷罗凯尔 クロケア Cassius", "玛琪艾儿 艾丝普莉 マチエール エスプリ Emma Essentia", "可可布尔 コンコンブル Gurkinn", "夜妮 ラニュイ Nita", "夕丝 ルスワール Evelyn", "昼珠 ラジュルネ Dana", "朝蜜 ルミタン Morgan", "阿卡马洛 アカマロ Chalmers", "凡篆 サカサ Inver", "基利 ギリー Aarune", "琉琪亚 ルチア Lisia", "希嘉娜 ヒガナ Zinnia", "葛蔓 カズラ Chaz", "茴璇 メグル Circie", "小阳* ヨウ Elio", "美月* ミヅキ Selene", "库库伊博士 ククイ博士 Professor Kukui", "妈妈 ママ Mom", "芭内特博士* バーネット博士 Professor Burnet", "莉莉艾 リーリエ Lillie", "哈乌 ハウ Hau", "伊利马 イリマ Ilima", "水莲 スイレン Lana", "玛奥 マオ Mallow", "马玛内 マーマネ Sophocles", "卡奇 カキ Kiawe", "阿塞萝拉 アセロラ Acerola", "茉莉 マツリカ Mina", "格拉吉欧 グラジオ Gladion", "布尔美丽 プルメリ Plumeria", "古兹马 グズマ Guzma", "露莎米奈 ルザミーネ Lusamine", "扎奥博 ザオボー Faba", "碧珂 ビッケ Wicke", "成也・大木 ナリヤ・オーキド Samson Oak", "哈拉 ハラ Hala", "丽姿 ライチ Olivia", "默丹 クチナシ Nanu", "哈普乌 ハプウ Hapu", "卡希丽 カヒリ Kahili", "马睿因 マーレイン Molayne", "龙葵 リュウキ Ryuki", "莫恩 モーン Mohn", "诗婷 ホウ Harper", "诗涵 スイ Sarah", "小驱* カケル Chase", "步美* アユミ Elaine", "小进* シン Trace", "碧蓝 ブルー Green", "小胜* マサル Victor", "小优* ユウリ Gloria", "妈妈 おかあさん Mum", "赫普 ホップ Hop", "丹帝 ダンデ Leon", "索妮亚 ソニア Sonia", "木兰博士 マグノリア博士 ‎ Professor Magnolia", "彼特 ビート Bede", "玛俐 マリィ Marnie", "洛兹 ローズ Rose", "奥利薇 オリーヴ Oleana", "亚洛 ヤロー Milo", "露璃娜 ルリナ Nessa", "卡芜 カブ Kabu", "彩豆 サイトウ Bea", "欧尼奥 オニオン Allister", "波普菈 ポプラ Opal", "玛瓜 マクワ Gordie", "美蓉 メロン Melony", "聂梓 ネズ Piers", "奇巴纳 キバナ Raihan", "阿球 ボールガイ Ball Guy", "邓培 ダンペイ Dan", "茂诗 ウカッツ Cara Liss", "索德 ソッド Sordward", "西尔迪 シルディ Shielbert", "克拉拉 クララ Klara", "赛宝利 セイボリー Avery", "马士德 マスタード Mustard", "蜜叶 ミツバ Honey", "海德 ハイド Hyde", "皮欧尼 ピオニー Peony", "夏科娅 シャクヤ Peonia", "明耀 テル Rei", "小照 ショウ Akari", "拉苯博士 ラベン博士 Professor Laventon", "星月 シマボシ Cyllene", "马加木 デンボク Kamado", "刚石 セキ Adaman", "珠贝 カイ Irida", "望罗 ウォロ Volo", "吾思 コギト Cogita", "石月 ムベ Beni", "阿米 ヨネ Mai", "菊伊 キクイ Lian", "夕蒲 ユウガオ Calaba", "火夏 ヒナツ Arezu", "阿芒 ススキ Iscan", "瓜娜 ガラナ Palina", "木春 ツバキ Melli", "滨廉 ハマレンゲ Gaeric", "山葵 ワサビ Sabi", "银仁 ギンナン Ginter", "贝里菈 ペリーラ Zisu", "晓白 タイサイ Choy", "纱珑 シャロン Anthe", "菜华 ナバナ Colza", "茶花 サザンカ Sanqua", "杵儿 キネ Pesselle", "桃发 タオファ Tao Hua", "亚白 ハク Rye", "阿松 オマツ Charm", "阿竹 オタケ Clover", "阿梅 オウメ Coin", "翠莉 ツイリ Tuli", "奥琳博士 オーリム博士 Professor Sada", "弗图博士 フトゥー博士 Professor Turo", "妮莫 ネモ Nemona");
}
