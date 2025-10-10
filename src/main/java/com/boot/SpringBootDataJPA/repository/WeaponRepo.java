package com.boot.SpringBootDataJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.boot.SpringBootDataJPA.model.Weapon;
@Component
public interface WeaponRepo extends JpaRepository<Weapon, Integer> {

}
