package sample.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "customer")
public class Customer extends Entity {

    private String name;
    private Address address;
    private LocalDateTime birthDate;

    private List<Event> events;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

}
