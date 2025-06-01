package net.crayonsmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    private ChatUtil() {
    }

    private static final Pattern START_WITH_COLOR_PATTERN = Pattern.compile(
            "^(?:[&§][0-9a-fk-or]|#[a-fA-F0-9]{6}).*",
            Pattern.CASE_INSENSITIVE
    );

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatUtil.format(message));
    }

    public static String format(String message) {
        Matcher matcher = START_WITH_COLOR_PATTERN.matcher(message);

        if (!matcher.matches()) {
            message = "&7" + message;
        }

        String translatedMessage = ChatColor.translateAlternateColorCodes('&', message);

        return hex(translatedMessage);
    }

    public static String hex(String message) {
        Pattern pattern = Pattern.compile("<#[a-fA-F0-9]{6}>");
        Matcher matcher = pattern.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String fullHexCode = matcher.group();
            String hexCode = fullHexCode.substring(1, fullHexCode.length() - 1); // e.g., #RRGGBB
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            matcher.appendReplacement(result, builder.toString());
        }

        matcher.appendTail(result);
        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }
}