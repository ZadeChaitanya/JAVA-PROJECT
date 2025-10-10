package com.boot.SpringBootDataJPA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.boot.SpringBootDataJPA.model.Weapon;
import com.boot.SpringBootDataJPA.repository.WeaponRepo;
@Component
public class WeaponRunner implements CommandLineRunner {
	@Autowired
	WeaponRepo wrepo;
	@Override
	public void run(String... args) throws Exception {
		//Insert

		
		//Single Receord Insert
		//save(obj)
		//Weapon obj=new Weapon(101, "AK-47", "Rifil");
		
//		wrepo.save(new Weapon(101, "AK-47", "Rifil"));
//		
//		System.out.println("Record Inserted Successfully");
		
//		Multiple Receord Insert
//		saveAll(iterator)
		/*
		List li=new ArrayList();
		
		li.add(new Weapon(102, "IN-SAS", "Rifil"));
		li.add(new Weapon(103, ".22", "Rifil"));
		li.add(new Weapon(104, "Tripad", "Machine Gun"));
		li.add(new Weapon(105, "Sinper", "Rifil"));
		
		wrepo.saveAll(li);
		System.out.println("Collection of Records Inserted");
	
		*/
		/*
		
		wrepo.saveAll(Arrays.asList(
				new Weapon(106, "Kalash", "Rifil"),
				new Weapon(107, "Sniper.02", "Rifil"),
				new Weapon(108, "Bhram", "Rifil")
				)						
				);
		System.out.println("Collection of Records Inserted");
		*/
		/*
		
		// All Records Fetch (findAll)
		
		List<Weapon> li=wrepo.findAll();
		
		for(Weapon w : li) {
			
			System.out.println(w.getWeapon_id());
			System.out.println(w.getWeapon_name());
			System.out.println(w.getWeapon_type());
		}
		*/
		
		/*
		//Single Record Fetch (findById)
		
		
		Optional<Weapon> op=wrepo.findById(103);
		
		
		if(op.isPresent()) {
			Weapon w=op.get();
			System.out.println(w.getWeapon_id());
			System.out.println(w.getWeapon_name());
			System.out.println(w.getWeapon_type());
		}
		*/
		
		//Delete Operation
		//Single Record Delete (deleteById(id) )
		/*
		wrepo.deleteById(101);
		
		System.out.println("Record Deleted");
		*/
		
		wrepo.deleteAll();
		System.out.println("All Record Deleted Successfully");
		 
		
		
	}

}
