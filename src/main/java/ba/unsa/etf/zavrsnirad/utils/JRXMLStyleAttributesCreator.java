package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLAttribute;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JRXMLStyleAttributesCreator {

    private static final String CSS_FONT_SIZE = "font-size";
    private static final String CSS_FONT = "font-family";
    private static final String CSS_BOLD = "font-weight";
    private static final String CSS_ITALIC = "font-style";
    private static final String CSS_TEXT_DECORATION = "text-decoration";
    private static final String CSS_UNDERLINE = "underline";
    private static final String CSS_LINE_THROUGH = "line-through";
    private static final String CSS_COLOR = "color";
    private static final String CSS_BACKGROUND_COLOR = "background-color";
    private static final String CSS_HORIZONTAL_ALIGNMENT = "text-align";


    private static final String JRXML_FONT_SIZE = "fontSize";
    private static final String JRXML_FONT = "fontName";
    private static final String JRXML_BOLD = "isBold";
    private static final String JRXML_ITALIC = "isItalic";
    private static final String JRXML_UNDERLINE = "isUnderline";
    private static final String JRXML_LINE_THROUGH = "isStrikeThrough";
    private static final String JRXML_COLOR = "forecolor";
    private static final String JRXML_BACKGROUND_COLOR = "backcolor";
    private static final String JRXML_HORIZONTAL_ALIGNMENT = "hTextAlign";


    public static List<JRXMLAttribute> generateStyles(List<JRXMLAttribute> jrxmlAttributeList) {
        List<JRXMLAttribute> newJrxmlAttributeList = new ArrayList<>();
        for (var jrxmlAttribute : jrxmlAttributeList) {
            switch (jrxmlAttribute.getId()) {
                case CSS_FONT_SIZE -> newJrxmlAttributeList.add(fontSizeAttribute(jrxmlAttribute.getValue()));
                case CSS_FONT -> newJrxmlAttributeList.add(fontAttribute(jrxmlAttribute.getValue()));
                case CSS_TEXT_DECORATION -> newJrxmlAttributeList.addAll(textDecorationAttributes(jrxmlAttribute.getValue()));
                case CSS_COLOR -> newJrxmlAttributeList.add(colorAttribute(jrxmlAttribute.getValue()));
                case CSS_BACKGROUND_COLOR -> newJrxmlAttributeList.add(backgorundColorAttribute(jrxmlAttribute.getValue()));
                case CSS_HORIZONTAL_ALIGNMENT -> newJrxmlAttributeList.add(horizontalAttribute(jrxmlAttribute.getValue()));
                case CSS_BOLD -> newJrxmlAttributeList.add(boldAttribute());
                case CSS_ITALIC -> newJrxmlAttributeList.add(italicAttribute());
            }
        }
        return newJrxmlAttributeList;
    }

    private static JRXMLAttribute fontSizeAttribute(String value) {
        int fontSize = switch (value) {
            case "x-small" -> 8;
            case "small" -> 10;
            case "large" -> 14;
            case "x-large" -> 18;
            case "xx-large" -> 24;
            case "-webkit-xxx-large" -> 36;
            default -> 12;
        };
        return new JRXMLAttribute(JRXML_FONT_SIZE, String.valueOf(fontSize));
    }

    private static JRXMLAttribute horizontalAttribute(String value) {
        return new JRXMLAttribute(JRXML_HORIZONTAL_ALIGNMENT,
                value.substring(0, 1).toUpperCase() + value.substring(1));
    }



    private static String parse(String input)
    {
        Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher m = c.matcher(input);
        Color color = Color.BLACK;

        if (m.matches())
        {
            color = Color.rgb(Integer.parseInt(m.group(1)),  // r
                    Integer.parseInt(m.group(2)),  // g
                    Integer.parseInt(m.group(3))); // b
        }

        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }

    private static JRXMLAttribute colorAttribute(String value) {
        return new JRXMLAttribute(JRXML_COLOR, parse(value));
    }

    private static JRXMLAttribute backgorundColorAttribute(String value) {
        return new JRXMLAttribute(JRXML_BACKGROUND_COLOR, parse(value).toString());
    }

    private static Collection<? extends JRXMLAttribute> textDecorationAttributes(String value) {
        List<JRXMLAttribute> jrxmlAttributeList = new ArrayList<>();
        var valueList = value.split(" ");
        for (var v : valueList) {
            if(v.equals(CSS_LINE_THROUGH)) {
                jrxmlAttributeList.add(new JRXMLAttribute(JRXML_LINE_THROUGH, "true"));
            }
            if(v.equals(CSS_UNDERLINE)) {
                jrxmlAttributeList.add(new JRXMLAttribute(JRXML_UNDERLINE, "true"));
            }
        }
        return jrxmlAttributeList;
    }

    private static JRXMLAttribute fontAttribute(String value) {
        return new JRXMLAttribute(JRXML_FONT, value);
    }

    private static JRXMLAttribute italicAttribute() {
        return new JRXMLAttribute(JRXML_ITALIC, "true");
    }

    private static JRXMLAttribute boldAttribute() {
        return new JRXMLAttribute(JRXML_BOLD, "true");
    }


}
