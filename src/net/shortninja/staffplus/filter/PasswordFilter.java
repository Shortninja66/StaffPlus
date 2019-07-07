package net.shortninja.staffplus.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public final class PasswordFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        return !record.getMessage().toLowerCase().contains("/register") && !record.getMessage().toLowerCase().contains("/login");
    }
}
