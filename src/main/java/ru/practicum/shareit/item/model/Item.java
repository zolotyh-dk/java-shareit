package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column(length = 1024)
    private String description;

    @Column
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "id")
    private User owner;


    private ItemRequest request;
}
