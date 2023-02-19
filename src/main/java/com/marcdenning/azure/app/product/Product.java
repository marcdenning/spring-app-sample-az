package com.marcdenning.azure.app.product;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "SalesLT", name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productid")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "productnumber")
    private String productNumber;

    @Column(name = "color")
    private String color;
}
