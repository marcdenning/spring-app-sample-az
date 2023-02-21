package com.marcdenning.azure.app.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
