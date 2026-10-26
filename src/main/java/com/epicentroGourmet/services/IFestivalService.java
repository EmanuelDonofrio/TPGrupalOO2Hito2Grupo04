package com.epicentroGourmet.services;

import com.epicentroGourmet.entities.Festival;

import java.util.List;

public interface IFestivalService {

    List<Festival> getAll();
    List<Festival> traerFestivalesPorUnidad(Long idUnidad);
}
