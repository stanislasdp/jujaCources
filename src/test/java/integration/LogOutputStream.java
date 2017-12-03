package integration;

import java.io.IOException;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream {

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void write(int b) throws IOException {

    }
}
