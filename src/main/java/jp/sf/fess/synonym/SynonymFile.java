package jp.sf.fess.synonym;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.Constants;

import org.apache.commons.io.IOUtils;

public class SynonymFile {
    private final File file;

    final List<SynonymItem> synonymItemList = new ArrayList<SynonymItem>();

    public SynonymFile(final File file) {
        this.file = file;
    }

    public void clear() {
        synchronized (synonymItemList) {
            synonymItemList.clear();
        }
    }

    public void load() {
        BufferedReader reader = null;
        synchronized (synonymItemList) {
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), Constants.UTF_8));
                int id = 0;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.length() == 0 || line.charAt(0) == '#') {
                        continue; // ignore empty lines and comments
                    }

                    String inputs[];
                    String outputs[];

                    final String sides[] = split(line, "=>");
                    if (sides.length > 1) { // explicit mapping
                        if (sides.length != 2) {
                            throw new IllegalArgumentException(
                                    "more than one explicit mapping specified on the same line");
                        }
                        final String inputStrings[] = split(sides[0], ",");
                        inputs = new String[inputStrings.length];
                        for (int i = 0; i < inputs.length; i++) {
                            inputs[i] = unescape(inputStrings[i]).trim();
                        }

                        final String outputStrings[] = split(sides[1], ",");
                        outputs = new String[outputStrings.length];
                        for (int i = 0; i < outputs.length; i++) {
                            outputs[i] = unescape(outputStrings[i]).trim();
                        }

                        if (inputs.length > 0 && outputs.length > 0) {
                            final SynonymItem item = new SynonymItem(id,
                                    inputs, inputs);
                            id++;
                            synonymItemList.add(item);
                        }
                    } else {
                        final String inputStrings[] = split(line, ",");
                        inputs = new String[inputStrings.length];
                        for (int i = 0; i < inputs.length; i++) {
                            inputs[i] = unescape(inputStrings[i]).trim();
                        }

                        if (inputs.length > 0) {
                            final SynonymItem item = new SynonymItem(id,
                                    inputs, inputs);
                            id++;
                            synonymItemList.add(item);
                        }
                    }
                }
            } catch (final IOException e) {
                throw new SynonymException("Failed to parse "
                        + file.getAbsolutePath(), e);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    private static String[] split(final String s, final String separator) {
        final List<String> list = new ArrayList<String>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        final int end = s.length();
        while (pos < end) {
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
                continue;
            }

            char ch = s.charAt(pos++);
            if (ch == '\\') {
                sb.append(ch);
                if (pos >= end) {
                    break; // ERROR, or let it go?
                }
                ch = s.charAt(pos++);
            }

            sb.append(ch);
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
        }

        return list.toArray(new String[list.size()]);
    }

    private String unescape(final String s) {
        if (s.indexOf("\\") >= 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                final char ch = s.charAt(i);
                if (ch == '\\' && i < s.length() - 1) {
                    sb.append(s.charAt(++i));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        return s;
    }

}
