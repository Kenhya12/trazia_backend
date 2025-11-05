package com.trazia.trazia_project.repository.product;

import com.trazia.trazia_project.entity.product.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLabelRepository extends JpaRepository<ProductLabel, String> {

}