package com.hotels.microservices.msvc_reservations.model;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "counters")
public class Counter {

    @Id
    private String id;
    private long seq;

    public void setId(String id) { this.id = id; }

    public void setSeq(long seq) { this.seq = seq; }
}
