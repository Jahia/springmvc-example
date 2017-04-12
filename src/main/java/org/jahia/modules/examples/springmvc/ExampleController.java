package org.jahia.modules.examples.springmvc;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A basic example of how to use Spring MVC annotated controllers with the Jahia CMS.
 */

@Controller
public class ExampleController {

    public class ComplexResult {
        private String firstName;
        private String lastName;

        public ComplexResult(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @RequestMapping(method= RequestMethod.GET,value="/hello",produces=MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getHello() {
        return "Hello World !";
    }

    @RequestMapping(method= RequestMethod.GET,value="/hello/{world}",produces=MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getHello(@PathVariable String world) {
        return "Hello " + world;
    }

    @RequestMapping(method= RequestMethod.GET,value="/complex",produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ComplexResult getComplexResult() {
        return new ComplexResult("Serge", "Huber");
    }

}
