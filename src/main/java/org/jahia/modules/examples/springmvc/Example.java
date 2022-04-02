package org.jahia.modules.examples.springmvc;

import org.springframework.stereotype.Component;

@Component
public class Example {
    private String message;

    public Example() {
        super();
    }

    public Example(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
