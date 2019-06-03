package de.nurmarvin.axo.utils;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AxolotlAPI {
    private static String[] AXOLOTL_IMAGES =
            {"https://axolotl.club/j6TNvHJ2.png?key=HailTheAxolotlVhfm3Q",
             "https://axolotl.club/42UHcXrM.jpg?key=HailTheAxolotlHPGhPx",
             "https://axolotl.club/NH8TL4od.gif?key=HailTheAxolotlBUTDP0",
             "https://axolotl.club/ZLYmqMXf.gif?key=HailTheAxolotl7okOL8",
             "https://axolotl.club/QBGYRx9m.jpg?key=HailTheAxolotlctg0lv",
             "https://axolotl.club/WkdWM6X3.jpg?key=HailTheAxolotlCFVJB0",
             "https://axolotl.club/wWe3sndn.jpg?key=HailTheAxolotlCiS4QC",
             "https://axolotl.club/KLLJYtZM.jpg?key=HailTheAxolotlX2ch3B",
             "https://axolotl.club/T33SUTuo.gif?key=HailTheAxolotl6wgNxQ",
             "https://axolotl.club/8ZB5zGr3.gif?key=HailTheAxolotlb5OIm8",
             "https://axolotl.club/vVudMBnh.jpg?key=HailTheAxolotlf2NFoB",
             "https://axolotl.club/jmgrXae7.jpg?key=HailTheAxolotlZnbY6N"};

    public static String randomAxolotl() {
        return AXOLOTL_IMAGES[ThreadLocalRandom.current().nextInt(AXOLOTL_IMAGES.length)];
    }
}
