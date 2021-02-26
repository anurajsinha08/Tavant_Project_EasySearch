package com.tavant.client.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.tavant.client.model.Address;
import com.tavant.client.model.FileInfo;
import com.tavant.client.model.Root;
import com.tavant.client.repository.AddressRepository;

@Service
public class FileService {

	@Autowired
	AddressRepository addressRepository;

	public ResponseEntity<?> store(MultipartFile file) throws IOException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		FileInfo FileInfo = new FileInfo(fileName, file.getContentType(), file.getBytes());

		Boolean res = false;
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
		file.transferTo(convFile);
		List<Address> authenticatedAddress = new ArrayList<Address>();
		List<Address> unAuthenticatedAddress = new ArrayList<Address>();
		List<Address> address = new ArrayList<Address>();
		// Using map to store the entire count of authenticated address stored in
		// database as well as keeping a count of unauthenticated address
		Map<String, Object> result = new LinkedHashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(convFile));
			String line = null;
			Scanner scanner = null;
			int index = 0;
			while ((line = reader.readLine()) != null) {
				Address add = new Address();
				scanner = new Scanner(line);
				scanner.useDelimiter(",");
				while (scanner.hasNext()) {
					String data = scanner.next();
					if (index == 1) {
						add.setCity(data);
					} else if (index == 2) {
						add.setConfidenceScore(Float.parseFloat(data));
					} else if (index == 3) {
						add.setDistrict(data);
					} else if (index == 4) {
						add.setELoc(data);
					} else if (index == 5) {
						add.setFormattedAddress(data);
					} else if (index == 6) {
						add.setGeocodeLevel(data);
					} else if (index == 7) {
						add.setHouseName(data);
					} else if (index == 8) {
						add.setHouseNumber(data);
					} else if (index == 9) {
						add.setLocality(data);
					} else if (index == 10) {
						add.setPincode(data);
					} else if (index == 11) {
						add.setPoi(data);
					} else if (index == 12) {
						add.setState(data);
					} else if (index == 13) {
						add.setStreet(data);
					} else if (index == 14) {
						add.setSubDistrict(data);
					} else if (index == 15) {
						add.setSubLocality(data);
					} else if (index == 16) {
						add.setVillage(data);
					} else
						System.out.println("Invalid");
					index++;
				}
				// Adding all address to a single list
				address.add(add);
				res = checkAddress(add);
				if (res) {
					// Adding validated address to the database and also to successful
					// authentication list
					addressRepository.save(add);
					authenticatedAddress.add(add);
				} else {
					unAuthenticatedAddress.add(add);
				}
				index = 0;
			}
			reader.close();

			result.put("Total_Address", authenticatedAddress.size() + unAuthenticatedAddress.size());
			result.put("Validated_Address", authenticatedAddress.size());
			result.put("UnAuthenticated_Address", unAuthenticatedAddress.size());
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("calling bad request");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	private boolean checkAddress(Address address) {

		String REST_SERVICE_URL = "http://apis.mapmyindia.com/advancedmaps/v1/6xoap5vq35wydtcocmxgr1laklk7efrj/place_detail?place_id=%s";
		String main_URL = String.format(REST_SERVICE_URL, address.getELoc());
		RestTemplate restTemplate = new RestTemplate();

		Root response = restTemplate.getForObject(main_URL, Root.class);

		if (response.getResponseCode() == 200 && response.getResults().size() > 0) {
			System.out.println(response.getResults());
			return true;
		}
		return false;
	}
}
