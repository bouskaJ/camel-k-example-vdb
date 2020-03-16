// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;
import org.apache.olingo.commons.api.ex.ODataException;

public class Vdb extends RouteBuilder {

  @Override
  public void configure() throws Exception {
      from("timer:java?period=10000&fixedRate=true")
        .to("olingo4://read/NOTE?serviceUri=http%3A%2F%2Fdv-dispatch%3A8080%2Fodata")
        .split()
          .simple("${body.entities}")
          .setBody()
            .simple("${body.properties}")
          .to("log:info");

      from("timer:java?period=5000&fixedRate=true")
        .setHeader("agent", simple("${random(1,10)}"))
        .setHeader("time", simple("${date:now+1h:HHss}"))
        .setHeader("CamelOlingo4.keyPredicate").simple("00${in.header.agent}")
        .doTry()
          // check if agent has an existing mission order
          .to("olingo4://read/NOTE?serviceUri=http%3A%2F%2Fdv-dispatch%3A8080%2Fodata")
          .log("Sending new mission order to agent 00${in.header.agent}.")
          // update mission order
          .setBody()
            .simple("{\"ID\": ${in.header.agent}, \"MSG\": \"Agent 00${in.header.agent}, you are go for next mission at ${in.header.time} hours\"}")          
          .to("olingo4://update/NOTE?serviceUri=http%3A%2F%2Fdv-dispatch%3A8080%2Fodata")
        .doCatch(ODataException.class)
          .onWhen(exceptionMessage().contains("Not Found"))
          // create new mission order
          .log("Sending agent 00${in.header.agent} on first mission.")
          .setBody()
            .simple("{\"ID\": ${in.header.agent}, \"MSG\": \"Congratulations agent 00${in.header.agent}! You are go for first mission at ${in.header.time} hours\"}")
          .to("olingo4://create/NOTE?serviceUri=http%3A%2F%2Fdv-dispatch%3A8080%2Fodata")
        .end();
  }
}
