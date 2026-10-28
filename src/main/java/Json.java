

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json {
    private final String s;
    private int pos;

    private Json(String s) {
        this.s = s;
    }

    public static Object parse(String text) {
        Json p = new Json(text);
        p.skipWs();
        Object v = p.readValue();
        p.skipWs();
        if (p.pos != p.s.length()) {
            throw p.error("лишние символы после JSON");
        }
        return v;
    }

    /** Экранирует строку и оборачивает в кавычки для вставки в JSON. */
    public static String quote(String str) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }

    private Object readValue() {
        if (pos >= s.length()) throw error("неожиданный конец JSON");
        char c = s.charAt(pos);
        switch (c) {
            case '{': return readObject();
            case '[': return readArray();
            case '"': return readString();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default: return readNumber();
        }
    }

    private Map<String, Object> readObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // {
        skipWs();
        if (peek() == '}') { pos++; return map; }
        while (true) {
            skipWs();
            if (peek() != '"') throw error("ожидалась строка-ключ");
            String key = readString();
            skipWs();
            if (peek() != ':') throw error("ожидалось ':'");
            pos++;
            skipWs();
            map.put(key, readValue());
            skipWs();
            char c = peek();
            pos++;
            if (c == '}') return map;
            if (c != ',') throw error("ожидалось ',' или '}'");
        }
    }

    private List<Object> readArray() {
        List<Object> list = new ArrayList<>();
        pos++; // [
        skipWs();
        if (peek() == ']') { pos++; return list; }
        while (true) {
            skipWs();
            list.add(readValue());
            skipWs();
            char c = peek();
            pos++;
            if (c == ']') return list;
            if (c != ',') throw error("ожидалось ',' или ']'");
        }
    }

    private String readString() {
        pos++; // открывающая кавычка
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (pos >= s.length()) throw error("незакрытая строка");
            char c = s.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            if (pos >= s.length()) throw error("незавершённый escape");
            char e = s.charAt(pos++);
            switch (e) {
                case '"' -> sb.append('"');
                case '\\' -> sb.append('\\');
                case '/' -> sb.append('/');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'u' -> {
                    if (pos + 4 > s.length()) throw error("неверный \\u escape");
                    sb.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
                    pos += 4;
                }
                default -> throw error("неизвестный escape: \\" + e);
            }
        }
    }

    private Double readNumber() {
        int start = pos;
        while (pos < s.length() && "+-0123456789.eE".indexOf(s.charAt(pos)) >= 0) pos++;
        if (start == pos) throw error("неожиданный символ '" + s.charAt(pos) + "'");
        return Double.parseDouble(s.substring(start, pos));
    }

    private void expect(String word) {
        if (!s.startsWith(word, pos)) throw error("ожидалось " + word);
        pos += word.length();
    }

    private char peek() {
        if (pos >= s.length()) throw error("неожиданный конец JSON");
        return s.charAt(pos);
    }

    private void skipWs() {
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
    }

    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException("Ошибка JSON на позиции " + pos + ": " + msg);
    }
}
