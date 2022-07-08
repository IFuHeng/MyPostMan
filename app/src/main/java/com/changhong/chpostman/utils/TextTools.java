package com.changhong.chpostman.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Iterator;

public class TextTools {
    public enum TextContentType {
        TEXT, JAVASCRIPT, JSON, HTML, XML
    }

    public static TextContentType guessTextType(String string) {

        if (string != null) {
            if ((string.contains("<!DOCTYPE html") || string.contains("<html>")) && string.contains("</html>")) {
                return TextContentType.HTML;
            } else if (string.startsWith("<?xml ")) {
                return TextContentType.XML;
            } else if (string.length() > 0
                    && ((string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') || (string.charAt(0) == '[' && string.charAt(string.length() - 1) == ']')))
                try {
                    new JSONObject(string);
                    return TextContentType.JSON;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        return TextContentType.TEXT;
    }

    public static String getCharsetFormHtml(String string) {
        int index = string.indexOf("charset=");
        if (index == -1)
            return Charset.defaultCharset().name();

        index += 8;
        int end = -1;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(index + i);
            if (c == '\'' || c == '\"' || c == '>' || c == '/') {
                end = i;
                break;
            }
        }

        if (end == -1)
            return Charset.defaultCharset().name();

        return string.substring(index, index + end);
    }

    public static CharSequence turnObjectToCharSequence(Object obj) {
        return turnObjectToCharSequence(obj, 0);
    }

    private static CharSequence turnObjectToCharSequence(Object obj, int level) {
        if (obj == null)
            return null;

        if (obj instanceof Number) {
            return createColorText(String.valueOf(obj), 0xFF229E93);
        } else if (obj instanceof String) {
            String string = obj.toString().trim();
            if (string.charAt(0) == '[') {
                try {
                    JSONArray ja = new JSONArray(string);
                    return turnObjectToCharSequence(ja, level);
                } catch (JSONException e) {
                    return string;
                }
            } else if (string.charAt(0) == '{') {
                try {
                    JSONObject ja = new JSONObject(string);
                    return turnObjectToCharSequence(ja, level);
                } catch (JSONException e) {
                    return string;
                }
            }
            createColorText('\"' + obj.toString().trim() + '\"', 0xFFB54827);
        } else if (obj instanceof JSONArray) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            JSONArray jsonArray = (JSONArray) obj;
            try {
                appendTabOr4Space(ssb, level);
                ssb.append('[').append(' ').append('\n');
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (i > 0)
                        ssb.append(',').append(' ').append('\n');
                    Object jitem = jsonArray.get(i);
                    if (!(jitem instanceof JSONObject) && !(jitem instanceof JSONArray)) {
                        appendTabOr4Space(ssb, level + 1);
                    }
                    ssb.append(turnObjectToCharSequence(jsonArray.get(i), level + 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ssb.append('\n');
            appendTabOr4Space(ssb, level);
            ssb.append(']').append(' ');
            return ssb;
        } else if (obj instanceof JSONObject) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            JSONObject jsonObject = (JSONObject) obj;
            int index = 0;
            Iterator<String> iterator = jsonObject.keys();
            appendTabOr4Space(ssb, level);
            ssb.append('{').append(' ').append('\n');
            while (iterator.hasNext()) {
                if (index++ > 0)
                    ssb.append(',').append(' ').append('\n');
                String key = iterator.next();
                appendTabOr4Space(ssb, level + 1);
                ssb.append(createColorText('\"' + key + '\"', 0xFFB54827));
                ssb.append(':').append(' ');
                try {
                    Object value = jsonObject.get(key);
                    if (value instanceof String) {
                        ssb.append(createColorText('\"' + value.toString() + '\"', 0xFF205791));
                    } else {
                        CharSequence charsequence = turnObjectToCharSequence(value, level + 1);
                        ssb.append(charsequence);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ssb.append("null");
                }
            }
            ssb.append('\n');
            appendTabOr4Space(ssb, level);
            ssb.append('}').append(' ');
            return ssb;
        }

        return obj.toString();
    }

    private static void appendTabOr4Space(SpannableStringBuilder ssb, int level) {
        for (int i = 0; i < level; i++)
            ssb.append("    ");
    }

//    public static CharSequence turnXmlInCharSequnce(InputStream is) throws ParserConfigurationException, IOException, SAXException {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //取得DocumentBuilderFactory实例
//        DocumentBuilder builder = factory.newDocumentBuilder(); //从factory获取DocumentBuilder实例
//        Document doc = builder.parse(is); //解析输入流 得到Document实例
//        Element rootElement = doc.getDocumentElement();
//        NodeList items = rootElement.getElementsByTagName("book");
//        for (int i = 0; i < items.getLength(); i++) {
//            Node item = items.item(i);
//            NodeList properties = item.getChildNodes();
//            for (int j = 0; j < properties.getLength(); j++) {
//                Node property = properties.item(j);
//                String nodeName = property.getNodeName();
//                if (nodeName.equals("id")) {
//                    book.setId(Integer.parseInt(property.getFirstChild().getNodeValue()));
//                } else if (nodeName.equals("name")) {
//                    book.setName(property.getFirstChild().getNodeValue());
//                } else if (nodeName.equals("price")) {
//                    book.setPrice(Float.parseFloat(property.getFirstChild().getNodeValue()));
//                }
//            }
//        }
//    }

    public static CharSequence string2CharSequence(String string, CharacterStyle... characterStyles) {
        if (string == null || string.length() == 0 || characterStyles == null || characterStyles.length == 0)
            return string;

        SpannableString ss = new SpannableString(string);
        for (CharacterStyle characterStyle : characterStyles) {
            ss.setSpan(characterStyle, 0, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    public static CharSequence turnHtmlToCharSequence(String str) {
        Document doc = Jsoup.parse(str);
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        for (int i = 0; i < doc.childNodeSize(); i++) {
            Node childNode = doc.childNode(i);
            if (childNode instanceof DocumentType) {
                ssb.append(createColorText(childNode.toString(), Color.GRAY));
            } else if (childNode instanceof TextNode) {
                nextLine(ssb);
                ssb.append(((TextNode) childNode).text());
            } else if (childNode instanceof Element) {
                nextLine(ssb);
                ssb.append(turnHtmlToCharSequence(childNode, 0));
            } else if (childNode instanceof Comment) {
                nextLine(ssb);
                ssb.append(createColorText("<!-- " + ((Comment) childNode).getData() + "-->", 0xff00cc00)).append('\n');
            }
        }

        return ssb;
    }

    private static CharSequence turnHtmlToCharSequence(Node node, int level) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        if (node instanceof TextNode) {
            String temp = ((TextNode) node).text();
            if (temp.trim().length() > 0)
                addTab(ssb, level).append(temp);
        } else if (node instanceof Element) {
            Element element = ((Element) node);
            nextLine(ssb);
            addTab(ssb, level).append(createColorText("<", Color.DKGRAY)).append(addTagName(element.tagName()));
            if (element.attributes() != null) {
                ssb.append(createAttributes(element.attributes()));
            }

            if (element.childNodeSize() > 0) {
                ssb.append(createColorText(">", Color.DKGRAY));
                boolean isSingleChildNode = element.childNodeSize() == 1 && element.childNode(0) instanceof TextNode;
                if (!isSingleChildNode) {
                    nextLine(ssb);
                    for (int i = 0; i < element.childNodeSize(); i++) {
                        if (i > 0) nextLine(ssb);
                        ssb.append(turnHtmlToCharSequence(element.childNode(i), level + 1));
                    }
                    nextLine(ssb);
                    addTab(ssb, level);
                } else
                    ssb.append(((TextNode) element.childNode(0)).text());

                ssb.append(createColorText("</", Color.DKGRAY))
                        .append(addTagName(element.tagName()))
                        .append(createColorText(">", Color.DKGRAY));
            } else {
                ssb.append(createColorText(element.tag().formatAsBlock() ? ">" : "/>", Color.DKGRAY));
            }
        } else if (node instanceof DataNode) {
            String temp = ((DataNode) node).getWholeData();
            addTab(ssb, level).append(createColorText(temp, Color.LTGRAY, 0.8f));
        } else if (node instanceof Comment) {
            String temp = "<!-- " + ((Comment) node).getData() + '[' + ((Comment) node).nodeName() + ']' + "-->";
            addTab(ssb, level).append(createColorText(temp, 0xff00cc00));
        }
        return ssb;
    }

    private static void nextLine(Editable editable) {
        if (editable.length() > 0 && editable.charAt(editable.length() - 1) != '\n')
            editable.append('\n');
    }

    private static Editable addTab(Editable editable, int level) {
        if (level <= 0)
            return editable;

        for (int i = 0; i < level; i++) {
            editable.append(' ').append(' ');
        }
        return editable;
    }

    private static CharSequence addTagName(String tag) {
        return createColorText(tag, Color.MAGENTA);
    }

    private static CharSequence createColorText(String text, int color) {
        return createColorText(text, color, 1);
    }

    private static CharSequence createColorText(String text, int color, float relativeSize) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), 0, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (relativeSize != 1 && relativeSize != 0) {
            ss.setSpan(new RelativeSizeSpan(relativeSize), 0, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    private static CharSequence createAttributes(Attributes attributes) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Attribute next : attributes) {
            ssb.append(' ');
            String key = next.getKey();
            ssb.append(createColorText(key, 0xFFFFA915));
            ssb.append(createColorText("=\"", 0xFF9A8868));
            String value = next.getValue();
            ssb.append(createColorText(value, Color.BLUE));
            ssb.append(createColorText("\"", 0xFF9A8868));
        }
        return ssb;
    }

    public static CharSequence turnXmlToCharSequence(String string) {
        // 创建一个xml解析的工厂
        try {
            XmlPullParserFactory factory = null;

            factory = XmlPullParserFactory.newInstance();

            // 获得xml解析类的引用
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(string));
            // 获得事件的类型
            final int COLOR_TAG = 0xff295999;
            final int COLOR_ATTR_KEY = 0xFF4B79FB;
            final int COLOR_ATTR_VALUE = 0xFF2EA34F;
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int eventType = parser.getEventType();
            int lastEventType = -1;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("TextTools", "====~ xml event type = " + XmlPullParser.TYPES[eventType]);
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        int depth = parser.getDepth();
                        int columnNumber = parser.getColumnNumber();
                        int lineNumber = parser.getLineNumber();
                        String nameSpace = parser.getNamespace();
                        String positionDescription = parser.getPositionDescription();
                        String prefix = parser.getPrefix();

                        if (ssb.length() == 0) {
                            ssb.append(createColorText("<?", COLOR_TAG));
                            ssb.append(createColorText("xml version", COLOR_ATTR_KEY));
                            ssb.append(createColorText("=\"" + parser.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-version") + '\"', COLOR_ATTR_VALUE));
                            ssb.append(' ');
                            ssb.append(createColorText("encoding", COLOR_ATTR_KEY));
                            ssb.append(createColorText("=\"" + parser.getInputEncoding() + '\"', COLOR_ATTR_VALUE));
                            ssb.append(createColorText("?>", COLOR_TAG));
                        }
                        ssb.append('\n');
                        addTab(ssb, parser.getDepth() - 1).append(createColorText('<' + name, COLOR_TAG));
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attr_name = parser.getAttributeName(i);
                            String attr_value = parser.getAttributeValue(i);
                            String attr_nameSpace = parser.getAttributeNamespace(i);
                            String attr_prefix = parser.getAttributePrefix(i);
                            String attr_type = parser.getAttributeType(i);
                            ssb.append('\n');
                            addTab(ssb, parser.getDepth()).append(createColorText(attr_name, COLOR_ATTR_KEY))
                                    .append(createColorText("=\"" + attr_value + '\"', COLOR_ATTR_VALUE));
                        }

                        ssb.append(createColorText(">", COLOR_TAG));
                        break;
                    case XmlPullParser.TEXT:
                        ssb.append(parser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        if (lastEventType == XmlPullParser.START_TAG) {
                            ssb.delete(ssb.length() - 1, ssb.length());
                            ssb.append(createColorText(" />", COLOR_TAG));
                        } else {
                            ssb.append('\n');
                            addTab(ssb, parser.getDepth() - 1).append(createColorText("</" + parser.getName() + '>', COLOR_TAG));
                        }
                        break;
                    case XmlPullParser.COMMENT:
                    case XmlPullParser.CDSECT:
                    case XmlPullParser.DOCDECL:
                    case XmlPullParser.ENTITY_REF:
                    case XmlPullParser.IGNORABLE_WHITESPACE:

                        break;
                }
                lastEventType = eventType;
                eventType = parser.next();
            }
            return ssb;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return string;
        }
    }
}
