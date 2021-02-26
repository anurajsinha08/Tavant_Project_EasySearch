package com.tavant.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tavant.client.model.FileInfo;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, String>{

}
