// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class Vdb extends RouteBuilder {

  @Override
  public void configure() throws Exception {
      from("timer:java?period=5s&fixedRate=true")
        .to("olingo4://read/NOTE?serviceUri=http%3A%2F%2Fdv-greeting%3A8080%2Fodata%2Faccounts")
        .to("log:info");

      from("timer:java?period=5s&fixedRate=true")
        .setBody()
          .simple("{\"id\": 1, \"msg\": \"foo\"}")
        .to("olingo4://create/NOTE?serviceUri=http%3A%2F%2Fdv-greeting%3A8080%2Fodata%2Faccounts")
        .to("log:info");
      
  }
}
