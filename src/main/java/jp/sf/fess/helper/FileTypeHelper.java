package jp.sf.fess.helper;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.util.StringUtil;

public class FileTypeHelper {
    protected String fieldName = "filetype_s";

    protected String defaultValue = "others";

    protected Map<String, String> mimetypeMap = new HashMap<String, String>();

    public void add(final String mimetype, final String filetype) {
        mimetypeMap.put(mimetype, filetype);
    }

    public String get(final String mimetype) {
        final String filetype = mimetypeMap.get(mimetype);
        if (StringUtil.isBlank(filetype)) {
            return defaultValue;
        }
        return filetype;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
