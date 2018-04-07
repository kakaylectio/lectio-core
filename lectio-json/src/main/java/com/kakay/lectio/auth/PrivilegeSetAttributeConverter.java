package com.kakay.lectio.auth;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;;

/**
 * Allows serialization of a Set<Privilige> into a single string for
 * storing in database
 *
 */
@Converter
public class PrivilegeSetAttributeConverter implements AttributeConverter<Set<Privilege>, String> {
	public PrivilegeSetAttributeConverter() {
	}
	@Override
	public String convertToDatabaseColumn(Set<Privilege> privilegeSet) {
		if (privilegeSet == null) {
			return null;
		}
		Set<String> privilegeStrings = new HashSet<String>();
		for(Privilege privilege :privilegeSet) {
			privilegeStrings.add(privilege.name());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String privilegeJson = "";
		try {
			privilegeJson = objectMapper.writeValueAsString(privilegeStrings);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return privilegeJson;
		
		
	}

	@Override
	public Set<Privilege> convertToEntityAttribute(String jsonString) {
		if (jsonString == null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String stringArray[] = objectMapper.readValue(jsonString, String[].class);
			Set<Privilege> privilegeSet =  new HashSet<Privilege>();
			for (String enumName: stringArray) {
				privilegeSet.add(Privilege.valueOf(enumName));
			}
			return privilegeSet;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashSet<Privilege>();
	}

}
