package com.mrozekma.atlassian.bitbucket.projectFields.servlet;

import java.util.HashMap;
import java.util.Map;

class ResultMessage {
    final String type, message;

    ResultMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    Map<String, String> toMap() {
        return new HashMap<String, String>() {{
            put("type", type);
            put("message", message);
        }};
    }
}
