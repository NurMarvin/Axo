package de.nurmarvin.axo.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter i, final Color c) throws IOException {
        i.beginObject();
        i.name("r").value(c.getRed());
        i.name("g").value(c.getGreen());
        i.name("b").value(c.getBlue());
        i.name("a").value(c.getAlpha());
        i.endObject();
    }

    @Override
    public Color read(JsonReader i) throws IOException {
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;

        i.beginObject();
        while (i.hasNext()) {
            switch (i.nextName()) {
                case "r":
                    r = i.nextInt();
                    break;
                case "g":
                    g = i.nextInt();
                    break;
                case "b":
                    b = i.nextInt();
                    break;
                case "a":
                    a = i.nextInt();
                    break;
            }
        }
        i.endObject();

        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        if (a > 255) a = 255;

        return new Color(r, g, b, a);
    }
}