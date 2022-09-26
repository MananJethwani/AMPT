package com.example.restservice.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.restservice.model.Product;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{name:'?0'}")
    Product findItemByName(String name);

    @Query("{url: '?0'}")
    Product findItemByUrl(String url);
    
    public long count();
}
