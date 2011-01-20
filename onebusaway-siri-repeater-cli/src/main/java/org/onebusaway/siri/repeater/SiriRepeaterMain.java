package org.onebusaway.siri.repeater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.onebusaway.siri.core.ESiriModuleType;
import org.onebusaway.siri.core.SiriClient;
import org.onebusaway.siri.core.SiriLibrary;
import org.onebusaway.siri.core.SiriRequestFactory;
import org.onebusaway.siri.core.SiriServer;
import org.onebusaway.siri.core.SiriSubscriptionManager;
import org.onebusaway.siri.core.exceptions.SiriException;
import org.onebusaway.siri.core.filters.SiriModuleDeliveryFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import uk.org.siri.siri.SubscriptionRequest;

public class SiriRepeaterMain {

  private static final String ARG_ID = "id";

  private static final String ARG_REPEATER_URL = "repeaterUrl";

  private static final String ARG_PRIVATE_REPEATER_URL = "privateRepeaterUrl";

  private static final String ARG_CLIENT_URL = "clientUrl";

  private static final String ARG_FILTER = "filter";

  private static final String ARG_PRIVATE_CLIENT_URL = "privateClientUrl";

  private static final String ARG_DATA_SOURCE = "dataSource";

  private static final String CLASSPATH_PREFIX = "classpath:";

  private static final String FILE_PREFIX = "file:";

  public static void main(String[] args) throws Exception {
    SiriRepeaterMain m = new SiriRepeaterMain();
    m.run(args);
  }

  public void run(String[] args) throws Exception {

    Options options = new Options();
    buildOptions(options);

    Parser parser = new PosixParser();
    CommandLine cli = parser.parse(options, args);

    args = cli.getArgs();

    if (args.length == 0) {
      printUsage();
      System.exit(-1);
    }

    List<String> paths = new ArrayList<String>();

    if (cli.hasOption(ARG_DATA_SOURCE))
      paths.add(cli.getOptionValue(ARG_DATA_SOURCE));

    paths.add(CLASSPATH_PREFIX
        + "org/onebusaway/siri/repeater/application-context.xml");

    ConfigurableApplicationContext context = createContext(cli, paths);

    handleCommandLineOptions(cli, context);

    SiriRepeater siriRepeater = context.getBean(SiriRepeater.class);
    SiriClient client = siriRepeater.getSiriClient();

    /**
     * Register a shutdown hook to clean up the repeater when things shutdown
     */
    Thread shutdownHook = new Thread(new ShutdownHookRunnable(siriRepeater));
    Runtime.getRuntime().addShutdownHook(shutdownHook);

    /**
     * Start it up
     */
    siriRepeater.start();

    SiriRequestFactory factory = new SiriRequestFactory();

    for (String arg : args) {

      Map<String, String> subArgs = SiriLibrary.getLineAsMap(arg);

      String url = subArgs.get("Url");
      if (url == null) {
        System.err.println("no url defined subscription request " + arg);
        continue;
      }

      SubscriptionRequest request = factory.createSubscriptionRequest(subArgs);
      client.handleSubscriptionRequest(url, request);
    }
  }

  private void printUsage() {
    System.err.println("usage:");
    System.err.println("  [-args] request [request ...]");
    System.err.println();
    System.err.println("args:");
    System.err.println("  -"
        + ARG_ID
        + "=id                          the SIRI participant id used by your client and server");
    System.err.println("  -"
        + ARG_CLIENT_URL
        + "=url                  the url your repeater client binds to and uses in publish/subscribe with your SIRI source");
    System.err.println("  -"
        + ARG_PRIVATE_CLIENT_URL
        + "=url           the internal url your repeater client will actually bind to, if specifed (default=clientUrl)");
    System.err.println("  -"
        + ARG_REPEATER_URL
        + "=url                the url your repeater server binds to and listens to incoming client requests");
    System.err.println("  -"
        + ARG_PRIVATE_REPEATER_URL
        + "=url         the internal url your repeater server will actually bind to, if specified (default=repeaterUrl)");
    System.err.println("  -"
        + ARG_DATA_SOURCE
        + "=path                a Spring context.xml file containing additional bean defs");
    System.err.println();
    System.err.println("request examples:");
    System.err.println("  Url=http://host:port/path,ModuleType=VEHICLE_MONITORING");
    System.err.println("  Url=http://host:port/path,ModuleType=VEHICLE_MONITORING,VehicleRef=1234");
  }

  protected void buildOptions(Options options) {
    options.addOption(ARG_ID, true, "SIRI client participant id");
    options.addOption(ARG_REPEATER_URL, true, "repeater url");
    options.addOption(ARG_PRIVATE_REPEATER_URL, true, "private repeater url");
    options.addOption(ARG_CLIENT_URL, true, "client url");
    options.addOption(ARG_PRIVATE_CLIENT_URL, true, "private client url");
    options.addOption(ARG_FILTER, true, "filter specification");
    options.addOption(ARG_DATA_SOURCE, true, "Spring data source xml file");
  }

  protected void handleCommandLineOptions(CommandLine cli,
      ConfigurableApplicationContext context) {

    SiriRepeater siriRepeater = context.getBean(SiriRepeater.class);

    SiriClient siriClient = siriRepeater.getSiriClient();
    SiriServer siriServer = siriRepeater.getSiriServer();
    SiriSubscriptionManager subscriptionManager = siriServer.getSubscriptionManager();

    /**
     * Handle command line options
     */
    if (cli.hasOption(ARG_ID)) {
      siriClient.setIdentity(cli.getOptionValue(ARG_ID));
      siriServer.setIdentity(cli.getOptionValue(ARG_ID));
    }
    if (cli.hasOption(ARG_CLIENT_URL))
      siriClient.setClientUrl(cli.getOptionValue(ARG_CLIENT_URL));
    if (cli.hasOption(ARG_PRIVATE_CLIENT_URL))
      siriClient.setPrivateClientUrl(cli.getOptionValue(ARG_PRIVATE_CLIENT_URL));
    if (cli.hasOption(ARG_REPEATER_URL))
      siriServer.setServerUrl(cli.getOptionValue(ARG_REPEATER_URL));
    if (cli.hasOption(ARG_PRIVATE_REPEATER_URL))
      siriServer.setPrivateServerUrl(cli.getOptionValue(ARG_PRIVATE_REPEATER_URL));

    /**
     * Filters
     */
    Map<String, SiriModuleDeliveryFilterMatch> filterSources = context.getBeansOfType(SiriModuleDeliveryFilterMatch.class);
    for (SiriModuleDeliveryFilterMatch filterSource : filterSources.values())
      subscriptionManager.addModuleDeliveryFilterSource(filterSource);

    if (cli.hasOption(ARG_FILTER)) {
      String filterSpec = cli.getOptionValue(ARG_FILTER);
      Map<String, String> filterArgs = SiriLibrary.getLineAsMap(filterSpec);

      String filterClassName = filterArgs.get("Class");

      SiriModuleDeliveryFilter filter = (SiriModuleDeliveryFilter) createObjectForClassName(filterClassName);

      SiriModuleDeliveryFilterMatch match = new SiriModuleDeliveryFilterMatch();
      match.setFilter(filter);

      String participant = filterArgs.get("Participant");
      if (participant != null)
        match.setParticipant(participant);

      String moduleType = filterArgs.get("ModuleType");
      if (moduleType != null)
        match.setModuleType(ESiriModuleType.valueOf(moduleType));

      subscriptionManager.addModuleDeliveryFilterSource(match);
    }
  }

  protected ConfigurableApplicationContext createContext(CommandLine cli,
      Iterable<String> paths) {

    Map<String, BeanDefinition> additionalBeans = new HashMap<String, BeanDefinition>();

    GenericApplicationContext ctx = new GenericApplicationContext();
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);

    for (String path : paths) {
      if (path.startsWith(CLASSPATH_PREFIX)) {
        path = path.substring(CLASSPATH_PREFIX.length());
        xmlReader.loadBeanDefinitions(new ClassPathResource(path));
      } else if (path.startsWith(FILE_PREFIX)) {
        path = path.substring(FILE_PREFIX.length());
        xmlReader.loadBeanDefinitions(new FileSystemResource(path));
      } else {
        xmlReader.loadBeanDefinitions(new ClassPathResource(path));
      }
    }

    for (Map.Entry<String, BeanDefinition> entry : additionalBeans.entrySet())
      ctx.registerBeanDefinition(entry.getKey(), entry.getValue());

    ctx.refresh();
    ctx.registerShutdownHook();
    return ctx;
  }

  private Object createObjectForClassName(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return clazz.newInstance();
    } catch (Throwable ex) {
      throw new SiriException("error instantiating class " + className, ex);
    }
  }

  private static class ShutdownHookRunnable implements Runnable {

    private final SiriRepeater _repeater;

    public ShutdownHookRunnable(SiriRepeater repeater) {
      _repeater = repeater;
    }

    @Override
    public void run() {
      _repeater.stop();
    }
  }
}
