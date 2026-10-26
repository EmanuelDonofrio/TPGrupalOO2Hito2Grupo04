package com.epicentroGourmet.services.implementation;

import com.epicentroGourmet.entities.Festival;
import com.epicentroGourmet.repositories.IFestivalRepository;
import com.epicentroGourmet.services.IFestivalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FestivalService implements IFestivalService {
    private final IFestivalRepository festivalRepository;

    public FestivalService(IFestivalRepository festivalRepository){
        this.festivalRepository = festivalRepository;
    }


    @Override
    public List<Festival> getAll(){
        return festivalRepository.findAll();
    }

    @Override
    public List<Festival> traerFestivalesPorUnidad(Long idUnidad){
        if (idUnidad == null){
            return List.of();
        }
        return festivalRepository.findFestivalesByUnidadVentaId(idUnidad);
    }



}
