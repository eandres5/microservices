package com.microservices.cliente_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.cliente_service.model.entities.Cliente;

@Repository
public interface ClienteDao extends JpaRepository<Cliente, Long>{


}
