package cn.edu.thu.log.test;
public class LogHead {
	String bookID;
	String name;
	String type;
	String author;
	double price;
	String publishDate;
	String publisher;
	String buyDate;
	String people;

	LogHead(String bookID, String name, String type, String author, double price,
			String publishDate, String publisher, String butDate, String people) {
		this.bookID = bookID;
		this.name = name;
		this.type = type;
		this.author = author;
		this.price = price;
		this.publishDate = publishDate;
		this.publisher = publisher;
		this.buyDate = butDate;
		this.people = people;
	}

	LogHead() {
	}
public String[] toArrays()
{
String[] strings={this.bookID,
this.name,
this.type ,
this.author ,
String.valueOf(this.price) ,
this.publishDate,
this.publisher,
this.buyDate ,
this.people,
};
return strings;
}
	public String getBookID() {
		return bookID;
	}

	public void setBookID(String bookID) {
		this.bookID = bookID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(String butDate) {
		this.buyDate = butDate;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

}
