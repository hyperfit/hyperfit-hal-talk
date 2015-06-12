# hyperfit-hal-talk
The purpose of this project is to demonstrate using Hyperfit to consume HAL Talk as a step by step guide.  Master contains the complete working example.  There are branches for each step & closed pull requests which show diffs about what was done for the step.  Most of the examples are implemented as test cases to simplify running within an IDE.

Some familiarity with HAL is advised, but not required.

This project consumes the hyper resources of the haltalk RESTful application hosted at http://haltalk.herokuapp.com

# 01 - Dependencies
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/01-dependencies) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/1/files)

To use hyperfit you must bring in the correct dependencies.  All projects must include the hyperfit-core.  From there, Hyperfit follows a plugin architecture at a minimum you will need a network client to issue requests and process responses and a handler for the content type the service uses.

The haltalk application uses https, so we will use the hyperfit-okhttp2 plugin that implements the hyperfit network stack for https (and a few other) protocols.  Since the haltalk application talks HAL (duh!) we also us the hyperfit-hal plugin.

Some transitive depenedencies are also now included in the project scope.  The okhttp2 plugin depends upon okhttp2.  The json parsing of the hyperfit-hal plugin uses jackson.  Hyperfit core has a few dependencies:
 - [Damn Hany URI Tempates](https://github.com/damnhandy/Handy-URI-Templates) for [RFC 6570 link templates](https://tools.ietf.org/html/rfc6570)  
 - slf4j-api - the actual logging implementation is up to app including Hyperfit
 - [Java Tuples](http://www.javatuples.org/) - a lightweight tuples implementation



# 02 - Contract Constants
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/02-contract-constants) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/2/files)

RESTful application are based around certain contract constants that cannot change without breaking backwards compatibility.  Some example include the root URL of the application, link relationships, & data field names.  It is a Hyperfit best practice to store all these values in a single class of constants, usually named ContractConstants.  This allows developers to know if some change has broken backwards compatibility.  At bbcom we've found this convention to be extremely useful.

The first constant defiend by the contract of any RESTful application is it's root URL so this has been added in the ContractConstants class.


# 03 - Root Retrieval
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/03-root-retrieval) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/3/files)

The first step in your RESTful client is to retrieve the root resource.  All other interaction with the service starts by utilizing the controls of the root resource, so let's go retrieve it!  

JUnit has been added as a test dependency to make it easy to execute use cases.  These tests effectively simulate real application code.

At bbcom we've found it very useful to debug http network traffic with [Charles](http://www.charlesproxy.com/).  As such we've added a profile to our maven configuration that sets up charles as an http/https proxy.  Additionally to avoid errors regarding self signed certificates issued by charles when it is in use as an man in the middle https proxy a helper method has been generated that creates an OkHTTP client that ignores all certificate errors.

The heart of a Hyperfit based client is the Hyperfit Processor.  This is the piece responsible for turning requests into responses, responses into generic hyper resources, and generic hyper resources into domain specific interfaces.  It contains all the knowledge and configuration about the execution pipeline and you must create one (and usually only one) in order to do anything useful with Hyperfit.  

In the new RootIntegrationTest::testRootRetrieval method a HyperfitProcessor is built that includes support for the HAL+JSON hyper resource content type (via the HalJsonContentTypeHandler instance) and uses OkHTTP2 network implementation to make http & https requests.  The most generic request for a resource is made:

```
HyperResource resource = processor.processRequest(HyperResource.class, ContractConstants.rootURL);
```

If you use charles to sniff this request you'll see the following request:
```
GET / HTTP/1.1
Accept: application/hal+json;q=1.0
Host: haltalk.herokuapp.com
Connection: Keep-Alive
Accept-Encoding: gzip
User-Agent: okhttp/2.1.0
```

Almost all of this is controlled by okhttp, the most notable piece is the Accept header which is requesting an application/hal+json representation of the root resource.  This mime type is provided by the HalJsonContentTypeHandler instance as it is the default mime type for that content type handler.

Running the test actual results in an error:

```
org.hyperfit.exception.ResponseException: Response from [https://haltalk.herokuapp.com/] with code [406] has unsupported content type [text/html; charset=utf-8]
```

What happened?  We requested application/hal+json but actually received text/html.  The Hyperfit Processor has no idea how to handle this content type as no handler for it was registered.  But we have a more fundamental problem as the service is clearly not respecting our requests as constructed.   We'll dive into that in the next section.

# 04 - Accept Header
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/04-accept-header) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/4/files)

In the last step we were receiving a 406 response code.  This indicates that the service did not understand any of our Accept header values.  Using the HAL Browser interface and sniffing the requests that work it turns out that the haltalk application expects application/json as the requested representation mime type as opposed to application/hal+json.  This is the type of information that is generally available in the documentation and is a cross cutting concern, meaning it applies to all interactions with the service.

Cross cutting concerns should be handled in the Hyperfit Processor, but we'll take a more step by step approach.  The testRootRetrievalForcedHeader method shows a very brute force method of manipulating the Accept header:
```
BoringRequestBuilder request = BoringRequestBuilder.get(ContractConstants.rootURL)
 .addAcceptedContentType("application/json")
 ;

HyperResource resource = processor.processRequest(HyperResource.class, request);
```

Here we use the BoringRequestBuilder to create a request for the root resoruce and add the required content type.  We then use one of the HyperfitProcessor processRequest overloads that takes a RequestBuilder instead of just a URL string to issue the actual request which ends up looking like this:
```
GET / HTTP/1.1
Accept: application/json,application/hal+json;q=1.0
Host: haltalk.herokuapp.com
Connection: Keep-Alive
Accept-Encoding: gzip
User-Agent: okhttp/2.1.0
```

The only difference is that the accept header now includes application/json.  The good news is that the response is now a 200 and contains the expected HAL+JSON response.  The bad news is that Hyperfit still throws an exception:
```
org.hyperfit.exception.ResponseException: Response from [https://haltalk.herokuapp.com/] with code [200] has unsupported content type [application/json; charset=utf-8]
```

Hyperfit doesn't know how to handle responses encoded as application/json.  As previsously mentioned the content type the service uses is a cross cutting concern, and as such the more appropriate place to configure content type configuration is in the Hyperfit Processor:

```
HyperfitProcessor processor = new HyperfitProcessor.Builder()
 .addContentTypeHandler(
    new HalJsonContentTypeHandler(),
    new ContentType("application", "json")
 )
 .hyperClient(new OkHttp2HyperClient(Helpers.allTrustingOkHttpClient()))
 .build();
```

Here we've told Hyperfit to use the HalJsonContentTypeHandler for both requests (and the Accept header in HTTP) and handling responses (defined by the Content-Type header in http).  No longer do we encounter a Hyperfit exception and thus we can actually use the HyperResource returned by the call to processRequest:

```
HyperResource resource = processor.processRequest(HyperResource.class, ContractConstants.rootURL);
 		 
System.out.println(resource.toString());

System.out.println("\nLinks:");
for(org.hyperfit.resource.controls.link.HyperLink link : resource.getLinks()){
  System.out.println(link.getRel() + " => " + link.getHref());
}

System.out.println("\nData:");
String welcome = resource.getPathAs(String.class, ContractConstants.DATA_FIELD_ROOT_WELCOME);
System.out.println("Welcome: " + welcome);
```
The first part of this code iterates over the hyperlink controls in the root resource and outputs their relationship and href.  Something to take note of is how the href are absolute URLs even though the actual response (viewable at https://haltalk.herokuapp.com/explorer/browser.html#/) only contains root relative urls.  The Hyperfit-HAL plugin uses the request URL as a base URL from which to calculate absolute URLs.

The second part of this code made use of the getPathAs method on HyperResource to retrieve the data identified by the *welcome* key which is referenced from the ContractConstants class.  The implementation of getPathAs is content type handler specific, for the HalJsonContentTypeHandler the implementation is almost competely dependent on Jackson deserialization.

# 05 - Adding an interface for data retrieval
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/05-interface-for-data) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/5/files)

You could certainly stop here and Hypefit would be useable, although not extremely valuable.  The getPathAs method is somewhat clunky and very raw.  Retrofit is so useful because it offers higher level abstraction of interfaces to developers, and Hyperfit has the same abstractions.  In this step we'll cover how to create a resource interface in an effort to make accessing the data of the root resouce very easy.

First we update the ContractConstants class with all the data field keys of the root resource.  These can come form the documentation of the service or just from looking at the actual response:
```
public static final String DATA_FIELD_ROOT_HINT_1 = "hint_1";
public static final String DATA_FIELD_ROOT_HINT_2 = "hint_2";
public static final String DATA_FIELD_ROOT_HINT_3 = "hint_3";
public static final String DATA_FIELD_ROOT_HINT_4 = "hint_4";
public static final String DATA_FIELD_ROOT_HINT_5 = "hint_5";
```

Next we create a resource interface representing the root that extends the base HyperResource interface.  A method is added for every field and annotated with the @Data annotations setting the data path to the field key.  The return type of this method is also of importance.  When this method is invoked the backing implementation generated by Hyperfit uses the value of the @Data annotation along with the return type of the method as parameters to the getPathAs method we used in the previous section.  In effect you can consider a resource interface's @Data annotated method as syntatic sugar for the getPathAs method.  Here's the Root resource interface with a method for accessing the welcome field, the same field we accessed in the previous step:

```
public interface Root extends HyperResource {

  @Data(ContractConstants.DATA_FIELD_ROOT_WELCOME)
  String getWelcome();
}
```

In effect this is the same as a call of ```getPathAs(String.class, ContractConstants.DATA_FIELD_ROOT_WELCOME)``` but clearly the interface method is a bit clearer and cleaner.

The final step is to tell the Hyperfit processor that you want to work with you new Root interface as opposed to the the generic HyperResource interface.  This is done by providing this interface as the parameter to the processRequest method.  Here we see this in action and some code accessing all the data fields of the root resource:
```
Root resource = processor.processRequest(Root.class, ContractConstants.rootURL);

System.out.println(resource.toString());
System.out.println("\nLinks:");
for(org.hyperfit.resource.controls.link.HyperLink link : resource.getLinks()){
  System.out.println(link.getRel() + " => " + link.getHref());
}

System.out.println("\nData:");
System.out.println("Welcome: " + resource.getWelcome());
System.out.println("Hint 1: " + resource.getHint1());
System.out.println("Hint 2: " + resource.getHint2());
System.out.println("Hint 3: " + resource.getHint3());
System.out.println("Hint 4: " + resource.getHint4());
System.out.println("Hint 5: " + resource.getHint5());
```

Here we've used a custom domain specific Resource Interface that acts a lot like a POJO.  This is certainly cool, but there's a LOT of different technologies out there that can take a URL or a string of JSON and turn it into a POJO to be used rather easily, if this is all that Hyperfit did it still wouldn't useful.

# 06 - Navigating to users
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/06-navigating-to-users) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/6/files)

What makes an application RESTful is that it provides Hyper Resources understandable by the client that contain both data and hypermedia controls.  We've seen how to make the Hyperfit client understand Hyper Resources via content type handlers and we've seen Hyperfit allow access to the data of a resource.  What is left is accessing and executing the hypermedia controls offered by the hyper resource.

Hypermedia controls present state transitions available to the client.  One of the most common and simple hypermedia control is the link.  It represents a state transition from one resource to another and is identified by the relationship between the two resources the link represents.  These relationships are very stable within a RESTful application and changing or removing a relationship would be a backwards incompatible modification, as such it makes a lot of sense to store the relationships in the ContractConstants class.

The root resource of the haltalk application has provides a few links, which we iterated over in a previous step.  The one we are going to execute is the navigation to the users resource.  This link has the ```ht:users``` relationship so that's the one we'll add to the ContractConstants class as ```REL_USERS```.

The logic pattner for executing a control in Hyperfit is resource -> control -> request -> resource.  We see this in action with the following code:
```
Root resource = processor.processRequest(Root.class, ContractConstants.rootURL);

RequestBuilder usersRequest = resource.getLink(ContractConstants.REL_USERS).toRequestBuilder();

HyperResource usersResource = processor.processRequest(HyperResource.class, usersRequest);

System.out.println("\nLinks:");
  for(org.hyperfit.resource.controls.link.HyperLink link : usersResource.getLinks()){
    System.out.println(link.getRel() + " => " + link.getHref());
}
```
Here we see a retrieval of the root resource and then a retrieval from that resource of the link hypermedia control identifed by the REL_USERS (ht:users) relationship.  This link is then converted into a RequestBuilder which is then processed via the HyperfitProcessor into a resulting HyperResource, very similar to the way we originally retrieved the Root resource just as a HyperResource.  Finally all the links of this new resource are iterated over and ouput.  We've successfully executed a hypermedia control to transition state from the root resource to this new resource.

Note: in this step you may notice that some of the boilerplate code that builds a HyperfitProcessor configured to work with the haltalk application has been moved to a helper function, we suggest this as a best practice and in general it usually useful to make the HyperfitProcessor instance a singleton.

# 07 - Adding links to the Resource Interface 
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/07-users-link-through-inteface) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/7/files)

In the previous step we transitions state from the root resource to another resource by using a link hypermedia control identified by it's relationship, specifically ht:users.  In this step we'll see that we can abstract the accessing of this link to the Root Resource Interface itself.  This is a small step to our ultimate goal of abstracting away links & requests and working directly with Resource Interfaces.

The code is very simple.  Add this to a Root resource interface:
```
@Link(ContractConstants.REL_USERS)
HyperLink getUsersLink();
```

Hyperfit's bakcing implementation to this method calls ```getLink(ContractConstants.REL_USERS)```, this is analagous to the @Data annotation.

# 08 - Following a link control
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/08-users-resource-through-follows) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/8/files)

The next step in our goal of working only with Resource Interfaces is to remove the transition from link to request.  By abstracting ourselves away from the request we no longer need a handle to the Hyperfit Processor!  Hyperfit still internally holds a reference but a client application need not pass the HyperfitProcessor instance to every method that wants to execute a resource's hyper media control.

This is possible because of the ```follow()``` method of Hyperfit's HyperLink class.  follow turns the link into a request and sends the request through the Hyperfit processor in one combined sequence.  It's another small step, but a very important one.  Here's the code in action:
```
//No longer need a reference to the processor!
Root root = Helpers.fetchRoot();

HyperResource usersResource = root.getUsersLink().follow(HyperResource.class);

System.out.println("\nLinks:");
for(org.hyperfit.resource.controls.link.HyperLink link : usersResource.getLinks()){
  System.out.println(link.getRel() + " => " + link.getHref());
}
```
In this example the root is fetched, the ht:users link control from the root resource is accessed, and then followed to a new hyper resource that has all of it's links iterated over and ouput.  Missing is any direct reference to the HyperfitProcessor itself, but it's still there doing all the hard work!


# 09 - Creating a Users resource interface
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/09-users-resource-interface) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/9/files)

The next step on the path to working only with Resource Interfaces is to build a Resource interface for the resoruce returned when following the ht:users link relationship instead of working with the generic HyperResource interface.

Choosing what to name your HyperResoruces can be tricky.  They don't have to follow the exact same conventions of the RESTful application you are consuming.  In this case we are building an interface for the resource browseable at https://haltalk.herokuapp.com/explorer/browser.html#/users and after a brief inspection it is pretty clear this resource contains only a bunch of ```ht:user``` link relationships to individual user resources.  As such we'll name this the users resource interface and include the ability to retrieve the collection of ```ht:user``` links:

```
public interface Users extends HyperResource {
  
  @Link(ContractConstants.REL_USER)
  HyperLink[] getUserLinks();
}
```
It's important to note that this resource interface, like all resource interfaces, must extend the HyperResource interface.  Another important convention introduced in this interface is the ability to return an array of HyperLink instances with the ht:user relationship instead of a single link as we had done previously.

The test applcation code uses the returned HyperLink array when iterating over all of the links:
```
Root root = Helpers.fetchRoot();
Users usersResource = root.getUsersLink().follow(Users.class);

System.out.println("\nLinks:");
for(org.hyperfit.resource.controls.link.HyperLink link : usersResource.getUserLinks()){
  System.out.println(link.getRel() + "(" + link.getTitle()  + ") => " + link.getHref());
}
```
Other things to note is that the param used in the follow() method is now the specific Users Resource Interface which has the getUserLinks method that is used to iterate with.  Also notice that each link has a relationship, title, and href accessory.


# 10 - Using only Resource Interfaces
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/10-resource-via-method) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/10/files)

The final step on our path to working with only Resource Interfaces is to remove links entirely.  Hyperfit provides this capability by adding a @Link annotated method to a Resource Interface that returns an interface that extends HyperResource like:
```
@Link(ContractConstants.REL_USERS)
Users users();
```

Calling this code executes the link contorl identified by the REL_USERS relationship and returns the result as a Users resource.  The backing implementation retrieves the given link and calls it's follows method (which in turn create a request that is passed to the HyperfitProcessor).  At this point the application test code works only with Resource Interfaces:
```
Root root = Helpers.fetchRoot();

//No more need to specify reflection class<t> stuff!
Users usersResource = root.users();

System.out.println("\nLinks:");
for(org.hyperfit.resource.controls.link.HyperLink link : usersResource.getUserLinks()){
  System.out.println(link.getRel() + "(" + link.getTitle()  + ") => " + link.getHref());
}
```
Which is a pretty powerful abstraction away from all the controls, requests, responses, and media types that are inherent to working with a service.

At this point it's important to bring up a rather important paper: [a note on distribute computing](http://www.eecs.harvard.edu/~waldo/Readings/waldo-94.pdf).  If you are unfamiliar it details how hiding the fact that a method may require requests distributed over a network form a developer can lead to very poor implementation that have disastorous performance.  Working only with Resource Interfaces makes it so the requests to the remote service are generally hidden from the user.  As such it's very important to indicate to any developers that a method call may incur network round trips.  At bbcom we settled into a pattern of prefixing method with get whenever we can guarantee that the execution will be done locally in memory with no network i/o.  Methods not prefixed with get *may* require network i/o and developers should work with those calls as they would any other network i/o based call.

# 11 - Retrieving Embedded Resources
[Branch](http://github.body.prod/hyperfit/hyperfit-hal-talk/tree/11-embedded-resources) & [Pull Request](http://github.body.prod/hyperfit/hyperfit-hal-talk/pull/11/files)

One of the greatest features of the HAL+JSON is the embedded resource hypermedia control.  This functions sort of like a prefetch where a link control is automatically executed and included in the response in effort to reduce the number of round trips the client need make.  Hyperfit supports HAL's embedded resource control natively.

Since an embedded resource is really just the prefetched execution of a link control Hyperfit provides access to the embedded resource by executing the link control.  The logic is very simple, before making the request for a link control, check if the control has already been prefetched and if so, return that!

In the haltalk application we see the embedded resource control in use when retrieving the latest posts via the ht:latest-post relationship from the root resource.  You can see this in action at https://haltalk.herokuapp.com/explorer/browser.html#/posts/latest where the resources of the ht:post relationship are embedded.

```
"_embedded": {
  "ht:post": [
    {
      "_links": {
        "self": {
          "href": "/posts/5179ada1e744480002000002"
        },
        "ht:author": {
          "href": "/users/drdamour",
          "title": "DaMour"
        }
      },
      "content": "damour is posting!",
      "created_at": "2013-04-25T22:26:41+00:00"
    },
    ...
  ]
}
```

