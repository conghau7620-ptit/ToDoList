package aws.demo.todolist.dto;

import java.util.ArrayList;
import java.util.List;

public class ToDoList {

	private Integer id;
	private String date;
	private String text;
	private List<String> images = new ArrayList<>();

	public ToDoList() {
		super();
	}

	public ToDoList(Integer id, String date, String text, List<String> images) {
		super();
		this.id = id;
		this.date = date;
		this.text = text;
		this.images = images;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "ToDoList [id=" + id + ", date=" + date + ", text=" + text + ", images=" + images + "]";
	}

	

}
