package aws.demo.todolist.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import aws.demo.todolist.dto.ToDoList;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Service
public class ToDoListService {
	private final DynamoDbClient client;
    private final String tableName;

    public ToDoListService(DynamoDbClient client, @Value("${aws.dynamodb.tableName}") String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public void createToDoList(ToDoList toDoList) {
        ToDoList item = new ToDoList(generateId(), toDoList.getDate(), toDoList.getText(), toDoList.getImages());
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", AttributeValue.builder().n(String.valueOf(item.getId())).build()); 
        itemValues.put("date", AttributeValue.builder().s(item.getDate()).build());
        itemValues.put("text", AttributeValue.builder().s(item.getText()).build());
        itemValues.put("images", AttributeValue.builder()
                .l(item.getImages().stream()
                        .map(s -> AttributeValue.builder().s(s).build())
                        .collect(Collectors.toList()))
                .build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            client.putItem(request);
        } catch (DynamoDbException e) {
            throw new RuntimeException("Error creating ToDo item: " + e.getMessage());
        }
    }

    public ToDoList getToDoList(Integer id) { // Changed to Integer
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("id", AttributeValue.builder().n(String.valueOf(id)).build())) // Use .n()
                .build();

        try {
            Map<String, AttributeValue> item = client.getItem(request).item();
            if (!item.isEmpty()) {
                String date = item.get("date").s();
                String text = item.get("text").s();
                List<String> images = item.get("images").l().stream()
                        .map(AttributeValue::s)
                        .collect(Collectors.toList());
                return new ToDoList(Integer.parseInt(item.get("id").n()), date, text, images); // Parse number to Integer
            }
            return null;
        } catch (DynamoDbException e) {
            throw new RuntimeException("Error retrieving ToDo item: " + e.getMessage());
        }
    }
    
    public List<ToDoList> getListToDoItems() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();
        try {
            return client.scan(scanRequest).items().stream()
                    .map(item -> new ToDoList(
                            Integer.parseInt(item.get("id").n()),
                            item.get("date").s(),
                            item.get("text").s(),
                            item.get("images").l().stream().map(AttributeValue::s).collect(Collectors.toList())
                    ))
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            throw new RuntimeException("Error retrieving ToDo items: " + e.getMessage());
        }
    }

    public void updateToDoList(Integer id, ToDoList toDoList) { // Changed to Integer
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("id", AttributeValue.builder().n(String.valueOf(id)).build())) // Use .n()
                .updateExpression("SET #t = :text, #i = :images")
                .expressionAttributeNames(Map.of(
                        "#t", "text",
                        "#i", "images"))
                .expressionAttributeValues(Map.of(
                        ":text", AttributeValue.builder().s(toDoList.getText()).build(),
                        ":images", AttributeValue.builder()
                                .l(toDoList.getImages().stream()
                                        .map(s -> AttributeValue.builder().s(s).build())
                                        .collect(Collectors.toList()))
                                .build()))
                .build();

        try {
            client.updateItem(request);
        } catch (DynamoDbException e) {
            throw new RuntimeException("Error updating ToDo item: " + e.getMessage());
        }
    }

    public void deleteToDoList(Integer id) { // Changed to Integer
        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("id", AttributeValue.builder().n(String.valueOf(id)).build())) // Use .n()
                .build();

        try {
            client.deleteItem(request);
        } catch (DynamoDbException e) {
            throw new RuntimeException("Error deleting ToDo item: " + e.getMessage());
        }
    }
    
    public Integer generateId() {
    	return (int) (System.currentTimeMillis() & Integer.MAX_VALUE);
    }
}
