package com.epicentroGourmet.repositories;

import com.epicentroGourmet.entities.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFestivalRepository extends JpaRepository<Festival, Long> {
}
