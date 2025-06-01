package aws.demo.todolist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

//public class LambdaHandler implements RequestHandler<String, String> {
//    private static final ApplicationContext context;
//
//    static {
//        try {
//            context = SpringApplication.run(TodolistApplication.class);
//        } catch (Exception e) {
//            // Log the error for debugging
//            System.err.println("Failed to initialize Spring context: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Spring context initialization failed", e);
//        }
//    }
//
//    @Override
//    public String handleRequest(String input, Context lambdaContext) {
//        lambdaContext.getLogger().log("Input: " + input); 
//        // Use Spring beans if needed
//        // MyService service = context.getBean(MyService.class);
//        return "Hello from Lambda: " + input;
//    }
//}

public class LambdaHandler implements RequestStreamHandler {

    private static final SpringBootProxyHandlerBuilder<AwsProxyRequest> handlerBuilder =
        new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
            .defaultProxy()
            .asyncInit()
            .springBootApplication(TodolistApplication.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
        throws IOException {
        try {
			handlerBuilder.build().proxyStream(inputStream, outputStream, context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContainerInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}