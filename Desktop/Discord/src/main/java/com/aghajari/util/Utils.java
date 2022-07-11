package com.aghajari.util;

import com.aghajari.shared.Profile;
import com.aghajari.shared.models.DeviceInfo;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.reflect.Method;

import javafx.scene.text.TextBoundsType;


public class Utils {

    public static WritableImage textToImage(String name) {
        String text = name.trim();
        if (text.length() > 2) {
            if (text.contains(" ")) {
                int a = text.indexOf(" ");
                text = "" + text.charAt(0) + text.charAt(a + 1);
            } else {
                text = text.substring(0, 2);
            }
        }

        Label label = new Label(text.toUpperCase());
        label.setMinSize(125, 125);
        label.setMaxSize(125, 125);
        label.setPrefSize(125, 125);
        label.setFont(Font.font("Arial", 60));
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #1763c6ff 0.0%, #5ed3f7ff 100.0%); -fx-text-fill: white;");
        label.setWrapText(true);
        Scene scene = new Scene(new Group(label));
        WritableImage img = new WritableImage(125, 125);
        scene.snapshot(img);
        return img;
    }

    public static <T extends Node> T findViewById(Node node, String id) {
        Node out = _findViewById(node, id);
        //noinspection unchecked
        return (T) (out == null ? node : out);
    }

    private static Node _findViewById(Node node, String id) {
        if (node == null)
            return null;

        if (node.getId() != null && node.getId().equals(id))
            return node;

        if (node instanceof Pane) {
            Pane p = (Pane) node;
            for (Node n : p.getChildren()) {
                Node n2 = _findViewById(n, id);
                if (n2 != null)
                    return n2;
            }
        } else if (node instanceof ScrollPane) {
            return _findViewById(((ScrollPane) node).getContent(), id);
        }
        return null;
    }

    public static void loadAvatar(Profile user, ImageView img) {
        if (user == null)
            return;
        loadAvatar(user, img, true);
    }

    public static void loadAvatar(Profile user, ImageView img, boolean text) {
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()
                && !user.getAvatar().endsWith("avatar.png")) {
            Image avatar = (user.getImage() == null || user.getImage().isError())
                    ? new Image(user.getAvatar(), true) : user.getImage();
            user.setImage(avatar);
            if (!user.getImage().isError() && avatar.getProgress() == 1) {
                img.setImage(avatar);
            } else {
                if (text)
                    img.setImage(textToImage(user.getAvatarName()));
                avatar.progressProperty().addListener((observableValue, number, t1) -> {
                    if (!user.getImage().isError() && t1.doubleValue() >= 1)
                        img.setImage(avatar);
                });
            }
        } else {
            if (text)
                img.setImage(textToImage(user.getAvatarName()));
        }
    }

    public static void loadBg(Profile user, Node bg, boolean gradient) {
        load(user, bg, gradient, "-fx-background-color", false);
    }

    public static void loadFill(Profile user, Node bg, boolean gradient) {
        load(user, bg, gradient, "-fx-fill", true);
    }

    private static void load(Profile user, Node bg, boolean gradient, String style, boolean bright) {
        if (user.getImage() == null)
            return;

        if (user.getColor() != null) {
            bg.setStyle(style + ": " + (gradient ? getColor(user, false) : toHex(bright ? user.getColor().brighter() : user.getColor())));
        } else {
            if (!user.getImage().isError() && user.getImage().getProgress() >= 1) {
                new Thread(() -> {
                    user.setColor(averageColor(user.getImage()));
                    Platform.runLater(() -> {
                        bg.setStyle(style + ": " + (gradient ? getColor(user, false) : toHex(bright ? user.getColor().brighter() : user.getColor())));
                    });
                }).start();
            } else {
                user.getImage().progressProperty().addListener((observableValue, number, t1) -> {
                    if (!user.getImage().isError() && t1.doubleValue() >= 1)
                        load(user, bg, gradient, style, bright);
                });
            }
        }
    }

    public static void loadTextColor(Profile user, Node bg, KeepPromise promise) {
        if (user.getImage() == null)
            return;

        if (user.getColor() != null) {
            bg.setStyle("-fx-text-fill: " + getColor(user, true));
        } else {
            if (!user.getImage().isError() && user.getImage().getProgress() >= 1) {
                new Thread(() -> {
                    if (user.getColor() == null)
                        user.setColor(averageColor(user.getImage()));
                    Platform.runLater(() -> {
                        bg.setStyle("-fx-text-fill: " + getColor(user, true));
                    });
                }).start();
            } else {
                user.getImage().progressProperty().addListener((observableValue, number, t1) -> {
                    if (!user.getImage().isError() && t1.doubleValue() >= 1)
                        if (promise.check()) {
                            loadTextColor(user, bg, promise);
                        }
                });
            }
        }
    }

    public interface KeepPromise {
        boolean check();
    }

    public static String getColor(Profile user, boolean brightFirst) {
        return "linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, "
                + toHex(brightFirst ? user.getColor().brighter() : user.getColor())
                + " 0.0%, "
                + toHex(brightFirst ? user.getColor() : user.getColor().brighter()) + " 100.0%);";
    }

    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public static String toHex(Color value) {
        return "#" + (format(value.getRed()) +
                format(value.getGreen()) +
                format(value.getBlue()) +
                format(value.getOpacity())).toUpperCase();
    }

    public static Color averageColor(Image bi) {
        double sumr = 0, sumg = 0, sumb = 0;
        int alphaZero = 0;
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color pixel = bi.getPixelReader().getColor(x, y);
                if (!pixel.isOpaque()) {
                    alphaZero++;
                    continue;
                }
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = (int) (bi.getWidth() * bi.getHeight()) - alphaZero;
        return Color.color(sumr / num, sumg / num, sumb / num);
    }

    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9]([_](?![_])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    private static final Pattern password = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    public static boolean isValidUsername(final String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean isValidPass(final String p) {
        Matcher matcher = password.matcher(p);
        return matcher.matches();
    }

    public static double getTextHeight(String text, Font f, double width) {
        try {
            Method m = Class.forName("com.sun.javafx.scene.control.skin.Utils").getDeclaredMethod("computeTextHeight",
                    Font.class, String.class, double.class, TextBoundsType.class);
            m.setAccessible(true);
            return (Double) m.invoke(null, f, text, width, TextBoundsType.LOGICAL_VERTICAL_CENTER) + 4;
        } catch (Exception e) {
            e.printStackTrace();
            return 32;
        }
    }

    public static double getTextWidth(String text, Font f, double max) {
        String[] lines = text.split("\\r?\\n");
        double lastWidth = 0;

        Text t = new Text();
        t.setFont(f);
        t.applyCss();
        for (String l : lines) {
            t.setText(l);
            double size = t.getLayoutBounds().getWidth();
            if (size > lastWidth)
                lastWidth = size;
        }
        return Math.min(max, lastWidth);
    }

    private static final Random rnd = new SecureRandom();
    private static final char[] symbols = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789").toCharArray();

    public static String rnd() {
        StringBuilder builder = new StringBuilder(12);
        for (int idx = 0; idx < 12; ++idx)
            builder.append(symbols[rnd.nextInt(symbols.length)]);
        return builder.toString();
    }

    public static String getComputerName() {
        String hostname = null;
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            hostname = env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            hostname = env.get("HOSTNAME");
        else {
            try {
                hostname = new BufferedReader(
                        new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream()))
                        .readLine();
                System.out.println(hostname);
            } catch (IOException ignore) {
                try {
                    InetAddress addr;
                    addr = InetAddress.getLocalHost();
                    hostname = addr.getHostName();
                } catch (Exception ignore2) {
                }
            }
        }

        if (hostname == null || hostname.trim().isEmpty())
            return "Unknown";
        else {
            hostname = hostname.trim();

            if (hostname.toLowerCase().endsWith(".local"))
                hostname = hostname.substring(0, hostname.length() - 6);

            hostname = hostname.replace('-', ' ');
            hostname = hostname.replace('/', ' ');
            hostname = hostname.replace('\\', ' ');
        }

        if (hostname.trim().isEmpty())
            return "Unknown";

        return hostname;
    }

    public static String getOperatingSystemName() {
        String OS = System.getProperty("os.name", "generic");
        if (OS == null || OS.trim().isEmpty())
            OS = "Unknown";
        return OS;
    }

    public static int getOperatingSystemType() {
        String OS = System.getProperty("os.name", "generic");
        if (OS.toLowerCase().contains("win"))
            return DeviceInfo.WINDOWS;
        return DeviceInfo.MAC;
    }

    public static Color getColorByIndex(Color fromColor, Color toColor, float percentage) {
        float percentage2 = 1 - percentage;
        return Color.rgb(
                (int) Math.round(255 * ((fromColor.getRed() * percentage) + (toColor.getRed() * percentage2))),
                (int) Math.round(255 * ((fromColor.getGreen() * percentage) + (toColor.getGreen() * percentage2))),
                (int) Math.round(255 * ((fromColor.getBlue() * percentage) + (toColor.getBlue() * percentage2)))
        );
    }
}
