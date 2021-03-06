springmvc-example
=================

An example Jahia module that includes a Spring MVC annotated controller

A few months ago, in a blog entry entitled “Fun with Jahia’s REST API”, I illustrated how straightforward yet
how powerful Jahia’s REST API can be. Since it gives you access and control over the full contents of the JCR
repository, and since almost everything in Jahia is stored in the repository, you can perform a lot of different
operations using the basic REST API. But what if you want to go further, access something that is either not exposed
through the basic API or maybe even expose your own services or back-end services using a REST API deployed in Jahia ?

I’ll show you how to achieve this using Spring annotated controllers, which are supported since
Jahia 6.6.1.7 (it is also possible to use them in earlier versions but it requires modifying the configuration of the
URLRewriteService, whereas in Jahia 6.6.1.7+ it works out of the box).

Before going any further, I’d like to talk about the benefits of Spring annotated controllers. The best way to
illustrate this is to first look at a piece of code, specifically a bean with Spring annotations. The first annotation
is on the class itself, simply indicating that the class is a Spring controller:

    @Controller
    public class ExampleController {

This is all that is needed for Spring to recognize this class as a Spring MVC controller. Now let’s look at a
method and its annotations:

    @RequestMapping(method= RequestMethod.GET,value="/hello",produces=MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getHello() {
        return "Hello World !";
    }

Despite the simple Java code, the annotations are almost longer than the code itself. However they do a great deal
for you such as mapping the method to the /hello URL path (paths are relative to the main Spring servlet, which in
Jahia is mapped at /cms, so the full path to the method will be /cms/hello), specifying the output format. The latter 
is a very powerful feature, since it means that you don’t have to bother with the output format at all.

So in the above example, by accessing the following URL http://localhost:8080/cms/hello, you will get an answer
that looks like this :

    Hello World !

The only remaining thing that is needed for this bean to be properly wired is to declare it in our Jahia module as
a Spring bean package that will be scanned for annotations. So we will add a springmvc-example.xml file
in our project in the src/main/resources/META-INF/spring directory, since these beans need to start at Jahia startup
to be properly registered. Inside the XML Spring descriptor file we have the following code:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd">
        <context:annotation-config/>
        <context:spring-configured/>
        <context:component-scan base-package="org.jahia.modules.examples.springmvc"/>
    
        <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
            <property name="order" value="0"/>
        </bean>
    
    </beans>

The first XML tag, called context:component-scan, is used to specify the base-package where to start scanning for
annotations and look for any Spring-supported annotations, including the MVC controller declarations we use in this
project. The second tag, called context:annotation-config is used to make sure that we initialize the default
annotation handler in Spring.

Now that this is all implemented and wired, all that is needed is to deploy your module to the 
digital-factory-data/modules directory or using the Administration UI to deploy or using a Maven jahia:deploy 
configuration.

The next step is actually using more complex methods to be able to not only generate responses but also to be able
to use request parameters. Here is another method example that we will detail:

    @RequestMapping(method= RequestMethod.GET,value="/hello/{world}",produces=MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getHello(@PathVariable String world) {
        return "Hello " + world;
    }

Most of the annotations are the same as in the case of the first method, but you will note that we now have a method
parameter called world of type String and that has a @PathVariable annotation. This annotation tells Spring annotations
handlers that they will need to resolve the value for this variable from the URL. If you look at the method mapping,
we have added a {world} marker that is used to indicate where in the URL we should look for a value for the world
method parameter. So for example URLs such as these will match the URL mapping :

    http://localhost:8080/cms/hello/serge
    http://localhost:8080/cms/hello/serge_huber

but these will not:

    http://localhost:8080/cms/hello/serge/test
    http://localhost:8080/cms/hello/ (this will actually map to the first method without parameters)

As you can see, it becomes possible, just by adding annotations to methods, to map them to URLs and expose a piece of
code using a simple REST API (provided of course you do your best to make the mappings conformant to REST principles).

We then use a response body to generate a String output that will use the value from the method parameter. So for a
request using the URL http://localhost:8080/cms/hello/serge we will get the following output :

    Hello serge

Of course in real projects simply outputting the input parameter is usually a good way to open your web services to
cross-site scripting issues, but for the sake of simplicity we did not make it more complex, but you should remain
aware that these risks exist and design your output generation accordingly.

Up until now, in the above examples, we have always generated simple string values as results of our method calls,
not really leveraging the power of the automatic serialization provided by Spring’s annotation handlers. So let’s
now present a more complex method:

    @RequestMapping(method= RequestMethod.GET,value="/complex",produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ComplexResult getComplexResult() {
        return new ComplexResult("Serge", "Huber");
    }

As you can see, we are now returning a custom class, that is defined as such :

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

If we access the mapped URL http://localhost:8080/cms/complex we get the following output:

	{"firstName":"Serge","lastName":"Huber"}

As you can see, Spring did all the work for us and generated a JSON output (which is the default output format since
we are using a web browser without any Accept headers) using the field names and values of the custom class. With very
little code, and almost no wiring code we managed to build a powerful HTTP API that can serve as a basis for much more
complex integrations.

We have only scratched the surface in this blog post as to what is possible with Spring’s annotated controllers, so
if you are interested in learning more, I suggest you read the Spring Web MVC documentation site. The combination of
this technology with all the content services and existing REST API provided by Jahia will help you quickly build
and deploy powerful services with minimal effort required. The additional benefit of these type of HTTP interfaces
is that they are also easy to integrate with native mobile applications, making the sky the limit as to the solutions
you can build. I can’t wait to see what you might come up with !