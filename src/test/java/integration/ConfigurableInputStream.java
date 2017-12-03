package integration;

import java.io.IOException;
import java.io.InputStream;

public class ConfigurableInputStream extends InputStream {

    private StringBuilder stringBuilder = new StringBuilder();
    private boolean isEndLine;

    @Override
    public int read()  {
        if (stringBuilder.length() == 0) {
            return -1;
        }

        if (isEndLine) {
            isEndLine = false;
            return -1;
        }

        char ch = stringBuilder.charAt(0);
        stringBuilder.deleteCharAt(0);

        if (ch == '\n') {
            isEndLine = true;
        }
        return ((int)ch);
    }

    public ConfigurableInputStream add(CharSequence charSequence) {
        stringBuilder
                .append(charSequence)
                .append("\n");
        return this;
    }

    public void reset() {
        stringBuilder = new StringBuilder();
    }
}
