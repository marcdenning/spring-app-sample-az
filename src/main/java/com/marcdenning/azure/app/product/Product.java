package com.marcdenning.azure.app.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * JPA Entity model of the SalesLT.Product table in the Azure SQL sample data set.
 */
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