package com.boot.SpringBootDataJPA.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Component
@Entity
@Table(name = "Weapons_table")
public class Weapon {
	@Id
	private Integer weapon_id;
	private String weapon_name;
	private String weapon_type;
	public Weapon() {
		super();
	
	}
	public Weapon(Integer weapon_id, String weapon_name, String weapon_type) {
		super();
		this.weapon_id = weapon_id;
		this.weapon_name = weapon_name;
		this.weapon_type = weapon_type;
	}
	public Integer getWeapon_id() {
		return weapon_id;
	}
	public void setWeapon_id(Integer weapon_id) {
		this.weapon_id = weapon_id;
	}
	public String getWeapon_name() {
		return weapon_name;
	}
	public void setWeapon_name(String weapon_name) {
		this.weapon_name = weapon_name;
	}
	public String getWeapon_type() {
		return weapon_type;
	}
	public void setWeapon_type(String weapon_type) {
		this.weapon_type = weapon_type;
	}
	@Override
	public String toString() {
		return "Weapon [weapon_id=" + weapon_id + ", weapon_name=" + weapon_name + ", weapon_type=" + weapon_type + "]";
	}
	
	
	
}
